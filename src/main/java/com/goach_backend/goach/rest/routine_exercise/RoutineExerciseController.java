package com.goach_backend.goach.rest.routine_exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.routine_exercise.RoutineExercise;
import com.goach_backend.goach.logic.entity.routine_exercise.RoutineExerciseRepository;
import com.goach_backend.goach.logic.entity.routine_exercise.RoutineExerciseSet;
import com.goach_backend.goach.logic.entity.routine_exercise.RoutineExerciseSetRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/routines/{routineId}/exercises")
public class RoutineExerciseController {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final RoutineExerciseRepository routineExerciseRepository;
    private final RoutineExerciseSetRepository routineExerciseSetRepository;

    public RoutineExerciseController(RoutineRepository routineRepository,
                                     ExerciseRepository exerciseRepository,
                                     RoutineExerciseRepository routineExerciseRepository,
                                     RoutineExerciseSetRepository routineExerciseSetRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.routineExerciseRepository = routineExerciseRepository;
        this.routineExerciseSetRepository = routineExerciseSetRepository;
    }

    // ---------- Exercises ----------

    @GetMapping
    public List<RoutineExercise> list(@PathVariable UUID routineId) {
        return routineExerciseRepository.findByRoutine_IdOrderByOrderIndexAsc(routineId);
    }

    @GetMapping("/{routineExerciseId}")
    public RoutineExercise get(@PathVariable UUID routineId, @PathVariable UUID routineExerciseId) {
        RoutineExercise e = routineExerciseRepository.findById(routineExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("RoutineExercise no existe"));
        if (!e.getRoutine().getId().equals(routineId)) {
            throw new IllegalArgumentException("El ejercicio no pertenece a la rutina");
        }
        return e;
    }

    /**
     * Crea un RoutineExercise. Se toma la rutina del path y el ejercicio del body por su ID.
     * Body esperado (ejemplo mínimo):
     * {
     *   "exercise": {"id": 5},
     *   "orderIndex": 1,
     *   "defaultRestSec": 90,
     *   "tempo": "3-0-3",
     *   "block": "A",
     *   "supersetGroup": "A1"
     * }
     */
    @PostMapping
    @Transactional
        public ResponseEntity<RoutineExercise> create(@PathVariable UUID routineId,
                                                  @Valid @RequestBody RoutineExercise body) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new IllegalArgumentException("Routine no existe"));
        if (body.getExercise() == null || body.getExercise().getId() == null) {
            throw new IllegalArgumentException("Debe indicar exercise.id");
        }
        Exercise exercise = exerciseRepository.findById(body.getExercise().getId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise no existe"));

        RoutineExercise entity = new RoutineExercise();
        entity.setRoutine(routine);
        entity.setExercise(exercise);
        entity.setOrderIndex(body.getOrderIndex());
        entity.setDefaultRestSec(body.getDefaultRestSec());
        entity.setTargetRpe(body.getTargetRpe());
        entity.setTargetRir(body.getTargetRir());
        entity.setTempo(body.getTempo());
        entity.setBlock(body.getBlock());
        entity.setSupersetGroup(body.getSupersetGroup());

        RoutineExercise saved = routineExerciseRepository.save(entity);
        return ResponseEntity.created(URI.create("/routines/" + routineId + "/exercises/" + saved.getId())).body(saved);
    }

    @PutMapping("/{routineExerciseId}")
    @Transactional
    public RoutineExercise update(@PathVariable UUID routineId,
                                  @PathVariable UUID routineExerciseId,
                                  @Valid @RequestBody RoutineExercise body) {
        RoutineExercise e = get(routineId, routineExerciseId);

        // si viene exercise.id, lo actualizamos
        if (body.getExercise() != null && body.getExercise().getId() != null) {
            Exercise ex = exerciseRepository.findById(body.getExercise().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Exercise no existe"));
            e.setExercise(ex);
        }
        if (body.getOrderIndex() != null) e.setOrderIndex(body.getOrderIndex());
        if (body.getDefaultRestSec() != null) e.setDefaultRestSec(body.getDefaultRestSec());
        if (body.getTargetRpe() != null) e.setTargetRpe(body.getTargetRpe());
        if (body.getTargetRir() != null) e.setTargetRir(body.getTargetRir());
        if (body.getTempo() != null) e.setTempo(body.getTempo());
        if (body.getBlock() != null) e.setBlock(body.getBlock());
        if (body.getSupersetGroup() != null) e.setSupersetGroup(body.getSupersetGroup());

        return e;
    }

    @DeleteMapping("/{routineExerciseId}")
    public ResponseEntity<Void> delete(@PathVariable UUID routineId, @PathVariable UUID routineExerciseId) {
        RoutineExercise e = get(routineId, routineExerciseId);
        routineExerciseRepository.delete(e);
        return ResponseEntity.noContent().build();
    }

    // ---------- Sets ----------

    @GetMapping("/{routineExerciseId}/sets")
    public List<RoutineExerciseSet> listSets(@PathVariable UUID routineId,
                                             @PathVariable UUID routineExerciseId) {
        get(routineId, routineExerciseId); // valida pertenencia
        return routineExerciseSetRepository.findByRoutineExercise_IdOrderBySetNumberAsc(routineExerciseId);
    }

    /**
     * Body mínimo:
     * { "setNumber":1, "minReps":8, "maxReps":10, "restSec":90, "targetPercentRm":75 }
     */
    @PostMapping("/{routineExerciseId}/sets")
    @Transactional
    public RoutineExerciseSet addSet(@PathVariable UUID routineId,
                                     @PathVariable UUID routineExerciseId,
                                     @Valid @RequestBody RoutineExerciseSet body) {
        RoutineExercise parent = get(routineId, routineExerciseId);
        RoutineExerciseSet s = new RoutineExerciseSet();
        s.setRoutineExercise(parent);
        s.setSetNumber(body.getSetNumber());
        s.setMinReps(body.getMinReps());
        s.setMaxReps(body.getMaxReps());
        s.setTargetWeight(body.getTargetWeight());
        s.setTargetPercentRm(body.getTargetPercentRm());
        s.setRestSec(body.getRestSec());
        s.setTargetRpe(body.getTargetRpe());
        s.setTargetRir(body.getTargetRir());
        return routineExerciseSetRepository.save(s);
    }

    @PutMapping("/{routineExerciseId}/sets/{setId}")
    @Transactional
    public RoutineExerciseSet updateSet(@PathVariable UUID routineId,
                                        @PathVariable UUID routineExerciseId,
                                        @PathVariable UUID setId,
                                        @Valid @RequestBody RoutineExerciseSet body) {
        get(routineId, routineExerciseId); // valida ejercicio
        RoutineExerciseSet s = routineExerciseSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set no existe"));
        if (!s.getRoutineExercise().getId().equals(routineExerciseId)) {
            throw new IllegalArgumentException("El set no pertenece al ejercicio");
        }
        if (body.getSetNumber() != null) s.setSetNumber(body.getSetNumber());
        if (body.getMinReps() != null) s.setMinReps(body.getMinReps());
        if (body.getMaxReps() != null) s.setMaxReps(body.getMaxReps());
        if (body.getTargetWeight() != null) s.setTargetWeight(body.getTargetWeight());
        if (body.getTargetPercentRm() != null) s.setTargetPercentRm(body.getTargetPercentRm());
        if (body.getRestSec() != null) s.setRestSec(body.getRestSec());
        if (body.getTargetRpe() != null) s.setTargetRpe(body.getTargetRpe());
        if (body.getTargetRir() != null) s.setTargetRir(body.getTargetRir());
        return s;
    }

    @DeleteMapping("/{routineExerciseId}/sets/{setId}")
    public ResponseEntity<Void> deleteSet(@PathVariable UUID routineId,
                                          @PathVariable UUID routineExerciseId,
                                          @PathVariable UUID setId) {
        get(routineId, routineExerciseId); // valida ejercicio
        RoutineExerciseSet s = routineExerciseSetRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set no existe"));
        if (!s.getRoutineExercise().getId().equals(routineExerciseId)) {
            throw new IllegalArgumentException("El set no pertenece al ejercicio");
        }
        routineExerciseSetRepository.delete(s);
        return ResponseEntity.noContent().build();
    }
}