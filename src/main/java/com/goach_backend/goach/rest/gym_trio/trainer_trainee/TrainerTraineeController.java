package com.goach_backend.goach.rest.gym_trio.trainer_trainee;

import com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee.TrainerTrainee;
import com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee.TrainerTraineeId;
import com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee.TrainerTraineeRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainers/{trainerId}/trainees")
public class TrainerTraineeController {

    private final UserRepository userRepository;
    private final TrainerTraineeRepository trainerTraineeRepository;

    public TrainerTraineeController(UserRepository userRepository,
                                    TrainerTraineeRepository trainerTraineeRepository) {
        this.userRepository = userRepository;
        this.trainerTraineeRepository = trainerTraineeRepository;
    }

    @GetMapping
    public List<TrainerTrainee> listByTrainer(@PathVariable UUID trainerId) {
        return trainerTraineeRepository.findByTrainer_Id(trainerId);
    }

    @GetMapping("/{traineeId}")
    public TrainerTrainee get(@PathVariable UUID trainerId, @PathVariable UUID traineeId) {
        return trainerTraineeRepository.findById(new TrainerTraineeId(trainerId, traineeId))
                .orElseThrow(() -> new IllegalArgumentException("Relación trainer-trainee no existe"));
    }

    /**
     * Crea la relación.
     * Body mínimo:
     * { "trainee": { "id": 123 },
     *   "traineeStatus": "ACTIVE",
     *   "traineePaymentStatus": "PAID",
     *   "traineePaymentDate": "2025-10-14T12:00:00-06:00"
     * }
     */
    @PostMapping
    public ResponseEntity<TrainerTrainee> create(@PathVariable UUID trainerId,
                                                 @RequestBody TrainerTrainee body) {
        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer no existe"));

        if (body.getTrainee() == null || body.getTrainee().getId() == null)
            throw new IllegalArgumentException("Debe indicar trainee.id");

        User trainee = userRepository.findById(body.getTrainee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Trainee no existe"));

        TrainerTrainee entity = new TrainerTrainee(trainer, trainee);
        entity.setTraineeStatus(body.getTraineeStatus());
        entity.setTraineePaymentStatus(body.getTraineePaymentStatus());
        entity.setTraineePaymentDate(body.getTraineePaymentDate());

        TrainerTrainee saved = trainerTraineeRepository.save(entity);
        return ResponseEntity.created(URI.create("/api/trainers/" + trainerId + "/trainees/" + trainee.getId()))
                .body(saved);
    }

    @PutMapping("/{traineeId}")
    public TrainerTrainee update(@PathVariable UUID trainerId, @PathVariable UUID traineeId,
                                 @RequestBody TrainerTrainee body) {
        TrainerTrainee entity = get(trainerId, traineeId);
        if (body.getTraineeStatus() != null) entity.setTraineeStatus(body.getTraineeStatus());
        if (body.getTraineePaymentStatus() != null) entity.setTraineePaymentStatus(body.getTraineePaymentStatus());
        if (body.getTraineePaymentDate() != null) entity.setTraineePaymentDate(body.getTraineePaymentDate());
        return entity;
    }

    @DeleteMapping("/{traineeId}")
    public ResponseEntity<Void> delete(@PathVariable UUID trainerId, @PathVariable UUID traineeId) {
        trainerTraineeRepository.deleteById(new TrainerTraineeId(trainerId, traineeId));
        return ResponseEntity.noContent().build();
    }

    // (Opcional) listar por trainee -> todos sus trainers
    @GetMapping("/by-trainee/{traineeId}")
    public List<TrainerTrainee> listByTrainee(@PathVariable UUID traineeId) {
        return trainerTraineeRepository.findByTrainee_Id(traineeId);
    }
}