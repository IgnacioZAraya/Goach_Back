package com.goach_backend.goach.rest.exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import com.goach_backend.goach.logic.entity.muscle_group.MuscleGroupEnum;
import com.goach_backend.goach.logic.entity.role.RoleEnum;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/exercise")
public class ExerciseRestController {
    @Autowired
    private ExerciseRepository exerciseRepository;

    @GetMapping
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    @GetMapping("/filterByName/{name}")
    public List<Exercise> getExerciseByName(@PathVariable String name) {
        return exerciseRepository.findExerciseByName(name);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> createExercise(@RequestBody Exercise exercise) {
        List<Exercise> exerciseAux = exerciseRepository.findExerciseByName(exercise.getName());

        if (!exerciseAux.isEmpty() && exerciseAux.getFirst().getName().equals(exercise.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Exercise already exist in the Data Base"));
        }

        Exercise saved = exerciseRepository.save(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{exerciseId}")
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> updateExercise(@PathVariable UUID exerciseId, @Valid @RequestBody Exercise body) {
        Exercise e = exerciseRepository.findById(exerciseId).orElseThrow(() -> new IllegalArgumentException("This exercise doesn¿t exists"));

        if (body.getName() != null) e.setName(body.getName());
        if (body.getDescription() != null) e.setDescription(body.getDescription());
        if (body.getMuscleGroup() != null) {
            try {
                MuscleGroupEnum mg = MuscleGroupEnum.valueOf(body.getMuscleGroup().name().toUpperCase());
                e.setMuscleGroup(mg);
            } catch (IllegalArgumentException ex) {
                System.out.println("Invalid muscle group: " + body.getMuscleGroup());
            }
        }


        Exercise saved = exerciseRepository.save(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{exerciseId}")
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> deleteExercise(@PathVariable UUID exerciseId) {
        Exercise e = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("This exercise doesn't exist"));

        exerciseRepository.delete(e);
        return ResponseEntity.noContent().build();
    }

}
