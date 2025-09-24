package com.goach_backend.goach.rest.workout_sessions;

import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/workout")
public class WorkoutSessionRestController {
    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @GetMapping
    public List<WorkoutSession> getAllWorkoutSession(){
        return workoutSessionRepository.findAll();
    }
    @GetMapping("/filterbytrainee/{email}")
    public List<WorkoutSession> getWorkoutSessionByTrainee(@PathVariable String email){
        return workoutSessionRepository.findWorkoutSessionByTrainee(email);
    }
    @GetMapping("/filterbystarteddate_finisheddate/{startedAt}/{finishedAt}")
    public List<WorkoutSession> getWorkoutSessionByTime(@PathVariable("startedAt") Date startedDate, @PathVariable("finishedAt") Date finishedDate){
        return workoutSessionRepository.findWorkoutSessionByStartedAtBetweenAndFinishedAt(startedDate, finishedDate);
    }
}
