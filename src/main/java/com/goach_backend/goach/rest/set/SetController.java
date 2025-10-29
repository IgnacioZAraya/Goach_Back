package com.goach_backend.goach.rest.set;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.set_exercise.Set;
import com.goach_backend.goach.logic.entity.set_exercise.SetExercise;
import com.goach_backend.goach.logic.entity.set_exercise.SetRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/sets")
@AllArgsConstructor
public class SetController {
    private final SetRepository setRepository;

    private final RoutineRepository routineRepository;

    private final ExerciseRepository exerciseRepository;

    @GetMapping
    public List<Set> listSets() {
        return setRepository.findAll();
    }

    @GetMapping("/filterByRoutine/{routineId}")
    public List<?> listRoutineSet(@PathVariable UUID routineId) {
        if (!routineRepository.existsById(routineId)) {
            return Collections.singletonList(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "La rutina no fue encontrada")));
        }

        return ResponseEntity.ok(setRepository.findByRoutine_IdOrderBySetNumberAsc(routineId)).getBody();
    }

    /**
     * Body mínimo:
     * { "setNumber":1, "minReps":8, "maxReps":10, "restSec":90, "targetPercentRm":75 }
     */
    @PostMapping("/{routineId}")
    @Transactional
    public Set addSet(@PathVariable UUID routineId,
                      @Valid @RequestBody Set body) {
        Routine parent = routineRepository.findById(routineId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La rutina no fue encontrada " + routineId));

        Set newSet = Set.builder()
                .setNumber(body.getSetNumber())
                .targetPRM(body.getTargetPRM())
                .targetRIR(body.getTargetRIR())
                .targetRPE(body.getTargetRPE())
                .restTime(body.getRestTime())
                .workTime(body.getWorkTime())
                .targetRPE(body.getTargetRPE())
                .targetRIR(body.getTargetRIR())
                .targetPRM(body.getTargetPRM())
                .build();

        newSet.setRoutine(parent);

        Set saved = setRepository.save(newSet);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved).getBody();
    }

    @PutMapping("/{setId}/routine/{routineId}")
    @Transactional
    public ResponseEntity<?> updateSet(@PathVariable UUID routineId,
                                       @PathVariable UUID setId,
                                       @Valid @RequestBody Set body) {
        Set s = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set no existe"));
        if (!s.getRoutine().getId().equals(routineId)) {
            throw new IllegalArgumentException("El set no pertenece a la rutina");
        }
        if (body.getSetNumber() != null) s.setSetNumber(body.getSetNumber());
        if (body.getWorkTime() != null) s.setWorkTime(body.getWorkTime());
        if (body.getRestTime() != null) s.setWorkTime(body.getRestTime());
        if (body.getTargetRPE() != null) s.setTargetRPE(body.getTargetRPE());
        if (body.getTargetRIR() != null) s.setTargetRIR(body.getTargetRIR());
        if (body.getTargetPRM() != null) s.setTargetPRM(body.getTargetPRM());

        Set saved = setRepository.save(s);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{setId}/routine/{routineId}")
    public ResponseEntity<Void> deleteSet(@PathVariable UUID routineId,
                                          @PathVariable UUID setId) {
        Set s = setRepository.findById(setId)
                .orElseThrow(() -> new IllegalArgumentException("Set no existe"));
        if (!s.getRoutine().getId().equals(routineId)) {
            throw new IllegalArgumentException("El set no pertenece al ejercicio");
        }
        setRepository.delete(s);
        return ResponseEntity.noContent().build();
    }
}
