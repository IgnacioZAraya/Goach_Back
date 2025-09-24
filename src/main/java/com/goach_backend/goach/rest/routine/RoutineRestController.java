package com.goach_backend.goach.rest.routine;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/routine")
public class RoutineRestController {
    @Autowired
    private RoutineRepository routineRepository;

    @GetMapping
    public List<Routine> getAllRoutines(){
        return routineRepository.findAll();
    }

    @GetMapping("/filterByName/{name}")
    public List<Routine> getExerciseByName(@PathVariable String name) {
        return routineRepository.findRoutineByName(name);
    }
}
