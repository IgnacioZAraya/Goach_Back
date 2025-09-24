package com.goach_backend.goach.rest.stats;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.stats.Stats;
import com.goach_backend.goach.logic.entity.stats.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsRestController {
    @Autowired
    private StatsRepository statsRepository;

    @GetMapping
    public List<Stats> getAllStats(){
        return statsRepository.findAll();
    }
    @GetMapping("/filterByWorkout/{id}")
    public List<Stats> getStatsByWorkout(@PathVariable String id) {
        return statsRepository.findStatsByWorkout(id);
    }
    @GetMapping("/filterByWorkout_Routine/{routineName}/completed_at/{completedAt}")
    public List<Stats> getStatsByWorkout(@PathVariable String routineName, @PathVariable Date completedAt) {
        return statsRepository.findStatsByWorkout_RoutineAndCompletedAt(routineName, completedAt);
    }
}
