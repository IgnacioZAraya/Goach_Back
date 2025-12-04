package com.goach_backend.goach.rest.stats;

import com.goach_backend.goach.logic.entity.stats.Stats;
import com.goach_backend.goach.logic.entity.stats.StatsRepository;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSessionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/stats")
public class StatsRestController {

    @Autowired
    private StatsRepository statsRepository;

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    /**
     * GET ALL STATS
     */
    @GetMapping
    public ResponseEntity<List<Stats>> getAllStats() {
        return ResponseEntity.ok(statsRepository.findAll());
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Stats>> getStatsByUser(@PathVariable UUID userId) {
        List<Stats> stats = statsRepository.findByWorkout_Trainee_Id(userId);
        return ResponseEntity.ok(stats);
    }


    /**
     * GET STATS BY WORKOUT SESSION ID
     */
    @GetMapping("/by-workout/{sessionId}")
    public ResponseEntity<?> getStatsByWorkout(@PathVariable UUID sessionId) {
        List<Stats> stats = statsRepository.findByWorkout_Id(sessionId);

        if (stats.isEmpty()) {
            return ResponseEntity.status(404).body("No stats found for workout session: " + sessionId);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * GET STATS BY ROUTINE NAME AND COMPLETION DATE
     * URL example:
     * /stats/filter?routineName=Bench&completedAt=2025-01-25T14:00:00
     */
    @GetMapping("/filter")
    public ResponseEntity<?> getStatsByRoutineAndCompletedAt(
            @RequestParam String routineName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date completedAt) {

        List<Stats> stats = statsRepository.findByWorkout_Routine_NameAndCompletedAt(routineName, completedAt);

        if (stats.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No stats found for routine '" + routineName + "' on date: " + completedAt);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * CREATE STATS
     */
    @PostMapping
    public ResponseEntity<?> createStats(@RequestBody Stats body) {

        if (body.getWorkout() == null || body.getWorkout().getId() == null) {
            return ResponseEntity.badRequest().body("Workout session is required");
        }

        Optional<WorkoutSession> workoutOpt = workoutSessionRepository.findById(body.getWorkout().getId());
        if (workoutOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Workout session not found");
        }

        body.setWorkout(workoutOpt.get());
        body.setCompletedAt(new Date());

        Stats saved = statsRepository.save(body);
        return ResponseEntity.ok(saved);
    }

    /**
     * UPDATE STATS
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStats(@PathVariable UUID id, @Valid @RequestBody Stats body) {

        Optional<Stats> existingOpt = statsRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Stats record not found");
        }

        Stats existing = existingOpt.get();

        // Update fields (only non-null fields)
        if (body.getDuration() != null) existing.setDuration(body.getDuration());
        if (body.getCalories() != null) existing.setCalories(body.getCalories());
        if (body.getActualRPE() != null) existing.setActualRPE(body.getActualRPE());
        if (body.getActualRIR() != null) existing.setActualRIR(body.getActualRIR());
        if (body.getActualPRM() != null) existing.setActualPRM(body.getActualPRM());

        Stats updated = statsRepository.save(existing);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE STATS
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStats(@PathVariable UUID id) {

        Optional<Stats> opt = statsRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Stats record not found");
        }

        statsRepository.delete(opt.get());
        return ResponseEntity.ok("Stats record deleted successfully");
    }
}