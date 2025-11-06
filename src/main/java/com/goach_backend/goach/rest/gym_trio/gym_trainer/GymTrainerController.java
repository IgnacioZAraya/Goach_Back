package com.goach_backend.goach.rest.gym_trio.gym_trainer;

import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTrainee;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTraineeRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainer;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerId;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import com.goach_backend.goach.logic.enums.AssocStatus;
import com.goach_backend.goach.logic.enums.MembershipState;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/gyms/{gymId}/trainers")
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

    @GetMapping("/{trainerId}")
    public GymTrainer getByTrainer(@PathVariable UUID gymId, @PathVariable UUID trainerId) {
        return gymTrainerRepository.findById(new GymTrainerId(gymId, trainerId)).orElseThrow(() -> new IllegalArgumentException("No existe relación gym-trainer"));
    }


    /**
     * Body mínimo: { "trainer": { "id": 77 }, "associateStatus":"ACTIVE", "gymPaymentStatus":"PAID" }
     */
    @PostMapping
    @Transactional
    public ResponseEntity<GymTrainer> create(
            @PathVariable UUID gymId,
            @RequestBody GymTrainer body
    ) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("Gym no existe"));

        if (body.getTrainer() == null || body.getTrainer().getEmail() == null)
            throw new IllegalArgumentException("Debe indicar trainer.email");

        User trainer = userRepository.findByEmail(body.getTrainer().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        AssocStatus assocStatus;
        try {
            assocStatus = AssocStatus.valueOf(body.getAssociateStatus().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de asociación inválido. Valores permitidos: ACTIVE, INACTIVE");
        }

        MembershipState paymentState;
        try {
            paymentState = MembershipState.valueOf(body.getGymPaymentStatus().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado de membresía inválido. Valores válidos: PENDING, ACTIVE, SUSPENDED, REMOVED");
        }

        GymTrainer entity = new GymTrainer(gym, trainer);
        entity.setAssociateStatus(assocStatus);
        entity.setGymPaymentDate(body.getGymPaymentDate());
        entity.setGymPaymentStatus(paymentState);
        entity.setGymPaymentPrice(body.getGymPaymentPrice());

        GymTrainer saved = gymTrainerRepository.save(entity);

        return ResponseEntity.created(URI.create("/gyms/" + gymId + "/trainers/" + trainer.getId()))
                .body(saved);
    }


    @PutMapping("/{trainerId}")
    @Transactional
    public GymTrainer update(@PathVariable UUID gymId, @PathVariable UUID trainerId, @RequestBody GymTrainer body) {
        GymTrainer entity = getByTrainer(gymId, trainerId);
        if (body.getAssociateStatus() != null) entity.setAssociateStatus(body.getAssociateStatus());
        if (body.getGymPaymentDate() != null) entity.setGymPaymentDate(body.getGymPaymentDate());
        if (body.getGymPaymentStatus() != null) entity.setGymPaymentStatus(body.getGymPaymentStatus());
        if (body.getGymPaymentPrice() != null) entity.setGymPaymentPrice(body.getGymPaymentPrice());
        return entity;
    }

    @DeleteMapping("/{trainerId}")
    public ResponseEntity<Void> delete(@PathVariable UUID gymId, @PathVariable UUID trainerId) {
        gymTrainerRepository.deleteById(new GymTrainerId(gymId, trainerId));
        return ResponseEntity.noContent().build();
    }
}