package com.goach_backend.goach.rest.gym_trio.gym_trainee;


import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTrainee;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTraineeId;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTraineeRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gyms/{gymId}/trainees")
public class GymTraineeController {

    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final GymTraineeRepository gymTraineeRepository;

    public GymTraineeController(GymRepository gymRepository, UserRepository userRepository,
                                GymTraineeRepository gymTraineeRepository) {
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
        this.gymTraineeRepository = gymTraineeRepository;
    }

    @GetMapping
    public List<GymTrainee> list(@PathVariable UUID gymId) {
        return gymTraineeRepository.findByGym_Id(gymId);
    }

    @GetMapping("/{traineeId}")
    public GymTrainee get(@PathVariable UUID gymId, @PathVariable UUID traineeId) {
        return gymTraineeRepository.findById(new GymTraineeId(gymId, traineeId))
                .orElseThrow(() -> new IllegalArgumentException("No existe inscripción"));
    }

    /**
     * Body mínimo: { "trainee": { "id": 123 }, "associateStatus":"ACTIVE", "membershipStatus":"PAID", "membershipPrice": 25000 }
     */
    @PostMapping
    @Transactional
    public ResponseEntity<GymTrainee> create(@PathVariable UUID gymId, @Valid @RequestBody GymTrainee body) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new IllegalArgumentException("Gym no existe"));

        if (body.getTrainee() == null || body.getTrainee().getId() == null)
            throw new IllegalArgumentException("Debe indicar trainee.id");

        User trainee = userRepository.findById(body.getTrainee().getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no existe"));

        GymTrainee entity = new GymTrainee(gym, trainee);
        entity.setAssociateStatus(body.getAssociateStatus());
        entity.setMembershipStatus(body.getMembershipStatus());
        entity.setMembershipPrice(body.getMembershipPrice());

        GymTrainee saved = gymTraineeRepository.save(entity);
        return ResponseEntity.created(URI.create("/api/gyms/" + gymId + "/trainees/" + trainee.getId()))
                .body(saved);
    }

    @PutMapping("/{traineeId}")
    @Transactional
    public GymTrainee update(@PathVariable UUID gymId, @PathVariable UUID traineeId,
                             @Valid @RequestBody GymTrainee body) {
        GymTrainee entity = get(gymId, traineeId);
        if (body.getAssociateStatus() != null) entity.setAssociateStatus(body.getAssociateStatus());
        if (body.getMembershipStatus() != null) entity.setMembershipStatus(body.getMembershipStatus());
        if (body.getMembershipPrice() != null) entity.setMembershipPrice(body.getMembershipPrice());
        return entity;
    }

    @DeleteMapping("/{traineeId}")
    public ResponseEntity<Void> delete(@PathVariable UUID gymId, @PathVariable UUID traineeId) {
        gymTraineeRepository.deleteById(new GymTraineeId(gymId, traineeId));
        return ResponseEntity.noContent().build();
    }
}