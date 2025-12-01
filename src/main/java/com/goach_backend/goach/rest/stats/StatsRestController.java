package com.goach_backend.goach.rest.stats;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.set_exercise.SetExerciseRepository;
import com.goach_backend.goach.logic.entity.stats.Stats;
import com.goach_backend.goach.logic.entity.stats.StatsRepository;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@RestController
@RequestMapping("/stats")
public class StatsRestController {

    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    WorkoutSessionRepository workoutSessionRepository;
    @Autowired
    SetExerciseRepository setExerciseRepository;

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    /**
     * GET ALL STATS
     */
    @GetMapping
    public ResponseEntity<List<Stats>> getAllStats() {
        return ResponseEntity.ok(statsRepository.findAll());
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
    public ResponseEntity<?> createStats(@Valid @RequestBody Stats body) {

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
    @PostMapping
    @Transactional
    public ResponseEntity<Stats> create(@RequestBody Stats body) {
        if (body.getId() == null) {
            body.setId(UUID.randomUUID());
        }
        // Validar que exista la sesión referida
        workoutSessionRepository.findById(body.getWorkout().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "WorkoutSession not found for session_id"));

        OffsetDateTime now = OffsetDateTime.now();
        body.setCreatedAt(now);
        body.setUpdatedAt(now);
        Stats saved = statsRepository.save(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    public Stats update(@PathVariable("id") UUID id, @RequestBody Stats body) {
        Stats current = statsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stats not found"));

        // Si cambian la session, valida que exista
        if (body.getWorkout().getId() != null && !body.getWorkout().getId().equals(current.getWorkout().getId())) {
            workoutSessionRepository.findById(body.getWorkout().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "WorkoutSession not found for session_id"));
            current.setWorkout(body.getWorkout());
        }

        current.setCalories(body.getCalories());
        current.setActualRpe(body.getActualRpe());
        current.setActualRir(body.getActualRir());
        current.setActualPrm(body.getActualPrm());
        current.setCompletedAt(body.getCompletedAt());
        current.setUpdatedAt(OffsetDateTime.now());
        return statsRepository.save(current);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        if (!statsRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Stats not found");
        }
        statsRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Endpoint combinado: Stats + max_weight =====
    // GET /api/stats/by-session/{sessionId}/with-max
    @GetMapping("/by-session/{workoutId}/with-max")
    public Map<String, Object> getStatsWithMaxWeight(@PathVariable("workoutId") UUID workoutId) {
        // 1) Obtener la sesión y su routineId
        WorkoutSession session = workoutSessionRepository.findById(workoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WorkoutSession not found"));

        // 2) Traer el Stats asociado a la sesión (si hay uno)
        Stats stats = statsRepository.findByWorkout_Id(workoutId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Stats not found for session"));

        // 3) Calcular MAX(max_weight) por routine_id
        Double maxWeight = setExerciseRepository.findMaxWeightByRoutineId(session.getRoutine().getId());

        // 4) Armar respuesta
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("stats", stats);
        resp.put("max_weight", maxWeight); // puede ser null si no hay datos
        resp.put("routine_id", session.getRoutine().getId());
        resp.put("session_id", workoutId);
        return resp;
    }
}
