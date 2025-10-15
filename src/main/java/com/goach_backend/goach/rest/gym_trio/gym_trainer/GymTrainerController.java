package com.goach_backend.goach.rest.gym_trio.gym_trainer;

import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainer;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerId;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gyms/{gymId}/trainers")
public class GymTrainerController {

    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final GymTrainerRepository gymTrainerRepository;

    public GymTrainerController(GymRepository gymRepository, UserRepository userRepository,
                                GymTrainerRepository gymTrainerRepository) {
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.gymTrainerRepository = gymTrainerRepository;
    }

    @GetMapping
    public List<GymTrainer> list(@PathVariable UUID gymId) {
        return gymTrainerRepository.findByGym_Id(gymId);
    }

    @GetMapping("/{trainerId}")
    public GymTrainer get(@PathVariable UUID gymId, @PathVariable UUID trainerId) {
        return gymTrainerRepository.findById(new GymTrainerId(gymId, trainerId))
                .orElseThrow(() -> new IllegalArgumentException("No existe relación gym-trainer"));
    }

    /**
     * Body mínimo: { "trainer": { "id": 77 }, "associateStatus":"ACTIVE", "gymPaymentStatus":"PAID" }
     */
    @PostMapping
    @Transactional
    public ResponseEntity<GymTrainer> create(@PathVariable UUID gymId, @RequestBody GymTrainer body) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("Gym no existe"));

        if (body.getTrainer() == null || body.getTrainer().getId() == null)
            throw new IllegalArgumentException("Debe indicar trainer.id");

        User trainer = userRepository.findById(body.getTrainer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        GymTrainer entity = new GymTrainer(gym, trainer);
        entity.setAssociateStatus(body.getAssociateStatus());
        entity.setGymPaymentDate(body.getGymPaymentDate());
        entity.setGymPaymentStatus(body.getGymPaymentStatus());

        GymTrainer saved = gymTrainerRepository.save(entity);
        return ResponseEntity.created(URI.create("/api/gyms/" + gymId + "/trainers/" + trainer.getId()))
                .body(saved);
    }

    @PutMapping("/{trainerId}")
    @Transactional
    public GymTrainer update(@PathVariable UUID gymId, @PathVariable UUID trainerId, @RequestBody GymTrainer body) {
        GymTrainer entity = get(gymId, trainerId);
        if (body.getAssociateStatus() != null) entity.setAssociateStatus(body.getAssociateStatus());
        if (body.getGymPaymentDate() != null) entity.setGymPaymentDate(body.getGymPaymentDate());
        if (body.getGymPaymentStatus() != null) entity.setGymPaymentStatus(body.getGymPaymentStatus());
        return entity;
    }

    @DeleteMapping("/{trainerId}")
    public ResponseEntity<Void> delete(@PathVariable UUID gymId, @PathVariable UUID trainerId) {
        gymTrainerRepository.deleteById(new GymTrainerId(gymId, trainerId));
        return ResponseEntity.noContent().build();
    }
}