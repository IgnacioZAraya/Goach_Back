package com.goach_backend.goach.rest.routine;

import com.goach_backend.goach.logic.entity.role.RoleEnum;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.set_exercise.Set;
import com.goach_backend.goach.logic.entity.set_exercise.SetExercise;
import com.goach_backend.goach.logic.entity.set_exercise.SetExerciseRepository;
import com.goach_backend.goach.logic.entity.set_exercise.SetRepository;
import com.goach_backend.goach.logic.entity.stats.Stats;
import com.goach_backend.goach.logic.entity.stats.StatsRepository;
import com.goach_backend.goach.logic.entity.trainee_routine.TraineeRoutine;
import com.goach_backend.goach.logic.entity.trainee_routine.TraineeRoutineRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSessionRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
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
@RequestMapping("/routine")
public class RoutineRestController {
    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraineeRoutineRepository traineeRoutineRepository;

    @Autowired
    private SetRepository setRepository;

    @Autowired
    private StatsRepository statsRepository;
    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;
    @Autowired
    private SetExerciseRepository setExerciseRepository;

    @GetMapping
    public List<Routine> getAllRoutines() {
        return routineRepository.findAll();
    }

    @GetMapping("/{routineId}")
    public ResponseEntity<?> getRoutineById(@PathVariable UUID routineId) {
        Optional<Routine> r = routineRepository.findById(routineId);

        if (r.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Routine not found or the provided Id is not valid"));
        }

        Routine requestedRoutine = r.get();

        return ResponseEntity.ok(requestedRoutine);
    }

    @GetMapping("/filterByName/{name}")
    public List<Routine> getRoutineByName(@PathVariable String name) {
        return routineRepository.findRoutineByName(name);
    }

    @GetMapping("/filterByUser/{userId}")
    public ResponseEntity<?> getRoutineByUserId(@PathVariable UUID userId) {
        List<Routine> r = routineRepository.findRoutineByTrainerId(userId);

        return ResponseEntity.ok(r);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> createRoutine(@Valid @RequestBody Routine routine) {
        UUID trainerId = routine.getTrainer().getId();

        Optional<User> trainer = userRepository.findById(trainerId);

        if (trainer.isEmpty() || trainer.get().getRole() != RoleEnum.TRAINER) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found or it is not a trainer"));
        }

        routine.setTrainer(trainer.get());

        Routine saved = routineRepository.save(routine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> updateRoutine(
            @PathVariable UUID id,
            @Valid @RequestBody Routine routine) {

        Optional<Routine> auxRoutine = routineRepository.findById(routine.getId());
        if (auxRoutine.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Routine not found"));
        }

        Routine existingRoutine = auxRoutine.get();

        Optional<User> trainerAux = userRepository.findById(id);
        if (trainerAux.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Trainer not found"));
        }

        User trainer = trainerAux.get();
        if (trainer.getRole() != RoleEnum.TRAINER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "The provided user is not a trainer"));
        }

        existingRoutine.setName(routine.getName());
        existingRoutine.setDescription(routine.getDescription());
        existingRoutine.setLevel(routine.getLevel());
        existingRoutine.setTrainer(trainer);

        Routine savedRoutine = routineRepository.save(existingRoutine);

        return ResponseEntity.ok(savedRoutine);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> deleteRoutine(@PathVariable UUID id) {
        if (!routineRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Routine not found"));
        }

        routineRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Routine permanently deleted"));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @PreAuthorize("hasAnyRole('TRAINER', 'ADMIN')")
    public ResponseEntity<?> deleteRoutine(@PathVariable UUID id) {

        Optional<Routine> routineOpt = routineRepository.findById(id);
        if (routineOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Routine not found"));
        }

        Routine routine = routineOpt.get();

        // ------------------------------------------------------------
        // 1. Eliminar SETS → pero antes eliminar SetExercises
        // ------------------------------------------------------------
        List<Set> sets = setRepository.findByRoutine_IdOrderBySetNumberAsc(id);

        for (Set set : sets) {
            // Eliminar SetExercises asociados al set
            List<SetExercise> exercises = setExerciseRepository.findBySet_IdOrderByOrderIndexAsc(set.getId());
            for (SetExercise se : exercises) {
                setExerciseRepository.delete(se);
            }

            // Eliminar el set
            setRepository.delete(set);
        }

        // ------------------------------------------------------------
        // 2. Eliminar WorkoutSessions → pero antes eliminar Stats
        // ------------------------------------------------------------
        List<WorkoutSession> sessions = workoutSessionRepository.findByRoutine_Id(id);

        for (WorkoutSession ws : sessions) {

            // Eliminar todas las Stats asociadas a esta Session
            statsRepository.deleteByWorkout_Id(ws.getId());

            // Eliminar la sesión
            workoutSessionRepository.delete(ws);
        }

        // ------------------------------------------------------------
        // 3. Eliminar TraineeRoutine
        // ------------------------------------------------------------
        traineeRoutineRepository.findByIdRoutineId(id);

        // ------------------------------------------------------------
        // 4. Eliminar la rutina
        // ------------------------------------------------------------
        routineRepository.delete(routine);

        return ResponseEntity.ok(Map.of(
                "message", "Routine and all related data deleted successfully",
                "deletedRoutineId", id
        ));
    }

}
