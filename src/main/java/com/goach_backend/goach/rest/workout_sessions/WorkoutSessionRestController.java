package com.goach_backend.goach.rest.workout_sessions;

import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.routine.RoutineRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/workout")
public class WorkoutSessionRestController {

    @Autowired
    private WorkoutSessionRepository workoutSessionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private GymRepository gymRepository;

    @GetMapping
    public ResponseEntity<List<WorkoutSession>> getAllWorkoutSessions() {
        return ResponseEntity.ok(workoutSessionRepository.findAll());
    }

    @GetMapping("/by-trainee/{email}")
    public ResponseEntity<?> getWorkoutSessionsByTrainee(@PathVariable String email) {
        List<WorkoutSession> list = workoutSessionRepository.findByTrainee_Email(email);

        if (list.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No workout sessions found for trainee: " + email);
        }

        return ResponseEntity.ok(list);
    }

    @GetMapping("/by-date")
    public ResponseEntity<?> getWorkoutSessionsByTime(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date end
    ) {

        List<WorkoutSession> list =
                workoutSessionRepository.findByStartedAtBetween(start, end);

        if (list.isEmpty()) {
            return ResponseEntity.status(404)
                    .body("No workout sessions found in the selected time range.");
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createWorkoutSession(@RequestBody WorkoutSession body) {
        Optional<User> traineeOpt = userRepository.findById(body.getTrainee().getId());
        if (traineeOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Trainee not found");
        }

        Optional<Routine> routineOpt = routineRepository.findById(body.getRoutine().getId());
        if (routineOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Routine not found");
        }

        Optional<Gym> gymOpt = Optional.empty();
        if (body.getGym() != null && body.getGym().getId() != null) {
            gymOpt = gymRepository.findById(body.getGym().getId());
            if (gymOpt.isEmpty()) {
                return ResponseEntity.status(404).body("Gym not found");
            }
        }

        WorkoutSession session = new WorkoutSession();
        session.setTrainee(traineeOpt.get());
        session.setRoutine(routineOpt.get());
        session.setGym(gymOpt.orElse(null));
        session.setStartedAt(body.getStartedAt());
        session.setFinishedAt(body.getFinishedAt());

        WorkoutSession saved = workoutSessionRepository.save(session);

        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkoutSession(@PathVariable UUID id) {

        Optional<WorkoutSession> opt = workoutSessionRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("WorkoutSession not found");
        }

        workoutSessionRepository.delete(opt.get());

        return ResponseEntity.ok("WorkoutSession deleted successfully");
    }
}
