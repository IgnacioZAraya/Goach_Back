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
