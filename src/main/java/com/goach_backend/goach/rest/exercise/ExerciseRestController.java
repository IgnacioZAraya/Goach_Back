package com.goach_backend.goach.rest.exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
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
    public List<Exercise> getAllExercises(){
        return exerciseRepository.findAll();
    }
    @GetMapping("/filterByName/{name}")
    public List<Exercise> getExerciseByName(@PathVariable String name) {
        return exerciseRepository.findExerciseByName(name);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> createExercise(@Valid @RequestBody Exercise exercise) {
        List<Exercise> exerciseAux = exerciseRepository.findExerciseByName(exercise.getName());

        if (!exerciseAux.isEmpty() && exerciseAux.getFirst().getName().equals(exercise.getName())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Exercise already exist in the Data Base"));
        }

        Exercise saved = exerciseRepository.save(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

}
