package com.goach_backend.goach.rest.gym_trio.trainer_trainee;

import com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee.TrainerTrainee;
import com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee.TrainerTraineeId;
import com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee.TrainerTraineeRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.sockets.LinkSocketHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("trainers/{trainerId}/trainees")
public class TrainerTraineeController {

    private final UserRepository userRepository;
    private final TrainerTraineeRepository trainerTraineeRepository;
    private final LinkSocketHandler linkSocketHandler;

    public TrainerTraineeController(UserRepository userRepository,
                                    TrainerTraineeRepository trainerTraineeRepository,
                                    LinkSocketHandler linkSocketHandler) {
        this.userRepository = userRepository;
        this.trainerTraineeRepository = trainerTraineeRepository;
        this.linkSocketHandler = linkSocketHandler;
    }

    @GetMapping
    public ResponseEntity<List<TrainerTrainee>> listByTrainer(@PathVariable UUID trainerId) {
        List<TrainerTrainee> ttList = trainerTraineeRepository.findByTrainer_Id(trainerId);

        if (ttList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        ttList.forEach(tt -> {
            Optional<User> trainee = userRepository.findById(tt.getId().getTraineeId());
            trainee.ifPresent(tt::setTrainee);
        });

        return ResponseEntity.ok(ttList);
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
     * "traineeStatus": "ACTIVE",
     * "traineePaymentStatus": "PAID",
     * "traineePaymentDate": "2025-10-14T12:00:00-06:00"
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
        return ResponseEntity.created(URI.create("trainers/" + trainerId + "/trainees/" + trainee.getId()))
                .body(saved);
    }

    @PostMapping("/linkRequest")
    public ResponseEntity<?> sendLinkRequest(@PathVariable UUID trainerId, @RequestBody User receiver) {
        User sender = userRepository.findById(trainerId).orElseThrow(() -> new IllegalArgumentException("Este trainer no existe"));
        User auxReceiver = userRepository.findByEmail(receiver.getEmail()).orElseThrow(() -> new IllegalArgumentException("Este usuario con " + receiver.getEmail() + " no existe"));

        linkSocketHandler.sendToUser(receiver.getId(), Map.of(
                "type", "link_request",
                "data", Map.of(
                        "senderId", sender.getId(),
                        "senderName", sender.getName(),
                        "receiverId", receiver.getId()
                )
        ));

        return ResponseEntity.ok(Map.of("status", "request_sent"));
    }

    @PostMapping("/rejectLinkRequest/{receiverId}")
    public ResponseEntity<?> rejectRequest(@PathVariable UUID trainerId, @PathVariable UUID receiverId) {
        linkSocketHandler.sendToUser(trainerId, Map.of(
                "type", "link_rejected",
                "data", Map.of("receiverId", receiverId)
        ));
        return ResponseEntity.ok(Map.of("status", "rejected"));
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