package com.goach_backend.goach.rest.exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.exercise.ExerciseRepository;
import com.goach_backend.goach.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
