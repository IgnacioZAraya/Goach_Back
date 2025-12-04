package com.goach_backend.goach.rest.set_exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import com.goach_backend.goach.logic.entity.set_exercise.SetExercise;
import com.goach_backend.goach.logic.entity.set_exercise.SetExerciseRepository;
import com.goach_backend.goach.logic.entity.set_exercise.Set;
import com.goach_backend.goach.logic.entity.set_exercise.SetRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/set/{setId}/exercises")
public class SetExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final SetExerciseRepository setExerciseRepository;
    private final SetRepository setRepository;

    public SetExerciseController(ExerciseRepository exerciseRepository,
                                 SetExerciseRepository setExerciseRepository,
                                 SetRepository setRepository) {
        this.exerciseRepository = exerciseRepository;
        this.setExerciseRepository = setExerciseRepository;
        this.setRepository = setRepository;
    }

    // ---------- Exercises ----------

    @GetMapping
    public List<SetExercise> list(@PathVariable UUID setId) {
        return setExerciseRepository.findBySet_IdOrderByOrderIndexAsc(setId);
    }

    @GetMapping("/{setExerciseId}")
    public SetExercise get(@PathVariable UUID setId, @PathVariable UUID setExerciseId) {
        SetExercise e = setExerciseRepository.findById(setExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("SetExercise no existe"));
        if (!e.getSet().getId().equals(setId)) {
            throw new IllegalArgumentException("El ejercicio no pertenece al set");
        }
        return e;
    }

    /**
     * Crea un RoutineExercise. Se toma la rutina del path y el ejercicio del body por su ID.
     * Body esperado (ejemplo mínimo):
     * {
     * "exercise": {"id": 5},
     * "orderIndex": 1,
     * "defaultRestSec": 90,
     * "tempo": "3-0-3",
     * "block": "A",
     * "supersetGroup": "A1"
     * }
     */
    @PostMapping
    @Transactional
    public ResponseEntity<SetExercise> create(@PathVariable UUID setId,
                                              @Valid @RequestBody SetExercise body) {
        Set set = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set no existe"));
        if (body.getExercise() == null || body.getExercise().getId() == null) {
            throw new IllegalArgumentException("Debe indicar exercise.id");
        }
        Exercise exercise = exerciseRepository.findById(body.getExercise().getId())
                .orElseThrow(() -> new IllegalArgumentException("Exercise no existe"));

        SetExercise entity = SetExercise.builder().set(set)
                .exercise(exercise)
                .orderIndex(body.getOrderIndex())
                .targetRPE(body.getTargetRPE())
                .targetRIR(body.getTargetRIR())
                .targetPRM(body.getTargetPRM())
                .createdAt(OffsetDateTime.now())
                .minReps(body.getMinReps())
                .maxReps(body.getMaxReps())
                .duration(body.getDuration())
                .maxWeight(body.getMaxWeight())
                .minWeight(body.getMinWeight())
                .build();

        SetExercise saved = setExerciseRepository.save(entity);
        return ResponseEntity.created(URI.create("/sets/" + setId + "/exercises/" + saved.getId())).body(saved);
    }

    @PutMapping("/{setExerciseId}")
    @Transactional
    public ResponseEntity<SetExercise> update(@PathVariable UUID setId,
                                              @PathVariable UUID setExerciseId,
                                              @Valid @RequestBody SetExercise body) {

        SetExercise entity = setExerciseRepository.findById(setExerciseId)
                .orElseThrow(() -> new IllegalArgumentException("SetExercise no existe"));

        if (!entity.getSet().getId().equals(setId)) {
            throw new IllegalArgumentException("SetExercise no pertenece al set indicado");
        }


        if (body.getExercise() != null && body.getExercise().getId() != null) {
            Exercise ex = exerciseRepository.findById(body.getExercise().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Exercise no existe"));
            entity.setExercise(ex);
        }

        if (body.getOrderIndex() != null) entity.setOrderIndex(body.getOrderIndex());
        if (body.getDuration() != null) entity.setDuration(body.getDuration());
        if (body.getMinReps() != null) entity.setMinReps(body.getMinReps());
        if (body.getMaxReps() != null) entity.setMaxReps(body.getMaxReps());
        if (body.getMinWeight() != null) entity.setMinWeight(body.getMinWeight());
        if (body.getMaxWeight() != null) entity.setMaxWeight(body.getMaxWeight());

        if (body.getTargetRPE() != null) entity.setTargetRPE(body.getTargetRPE());
        if (body.getTargetRIR() != null) entity.setTargetRIR(body.getTargetRIR());
        if (body.getTargetPRM() != null) entity.setTargetPRM(body.getTargetPRM());

        SetExercise saved = setExerciseRepository.save(entity);
        return ResponseEntity.ok(saved);
    }


    @DeleteMapping("/{setExerciseId}")
    public ResponseEntity<Void> delete(@PathVariable UUID setId, @PathVariable UUID setExerciseId) {
        SetExercise e = get(setId, setExerciseId);
        setExerciseRepository.delete(e);
        return ResponseEntity.noContent().build();
    }
}