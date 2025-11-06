package com.goach_backend.goach.rest.gym;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.gym.GymRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTrainee;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainee.GymTraineeRepository;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainer;
import com.goach_backend.goach.logic.entity.gym_trio.gym_trainer.GymTrainerRepository;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/gym")
public class GymRestController {
    @Autowired
    private GymRepository gymRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GymTraineeRepository gymTraineeRepository;
    @Autowired
    private GymTrainerRepository gymTrainerRepository;

    @GetMapping
    public List<Gym> getAllGym() {
        return gymRepository.findAll();
    }

    @GetMapping("/filterByName/{name}")
    public List<Gym> getGymByName(@PathVariable String name) {
        return gymRepository.findGymByName(name);
    }

    public record GymPopulationResponse(
            UUID id,
            String name,
            User owner,
            int totalPopulation
    ) {
    }

    @GetMapping("/{ownerId}")
    public ResponseEntity<GymPopulationResponse> get(@PathVariable UUID ownerId) {
        Gym gym = gymRepository.findByOwner_Id(ownerId);

        if (gym == null) {
            GymPopulationResponse emptyResponse = new GymPopulationResponse(
                    null,
                    "",
                    null,
                    0
            );
            return ResponseEntity.ok(emptyResponse);
        }

        List<GymTrainer> trainers = gymTrainerRepository.findByGym_Id(gym.getId());
        List<GymTrainee> trainees = gymTraineeRepository.findByGym_Id(gym.getId());

        List<User> trainerUsers = trainers.stream()
                .map(GymTrainer::getTrainer)
                .toList();

        List<User> traineeUsers = trainees.stream()
                .map(GymTrainee::getTrainee)
                .toList();

        int totalPopulation = trainerUsers.size() + traineeUsers.size();

        GymPopulationResponse response = new GymPopulationResponse(
                gym.getId(),
                gym.getName(),
                gym.getOwner(),
                totalPopulation
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Gym body) {
        if (body.getOwner() == null || body.getOwner().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Owner is required"));
        }

        Optional<User> ownerOpt = userRepository.findById(body.getOwner().getId());
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Owner not found"));
        }

        Gym gym = new Gym();
        gym.setName(body.getName());
        gym.setOwner(ownerOpt.get());

        gymRepository.save(gym);

        return ResponseEntity.status(HttpStatus.CREATED).body(gym);
    }

    @PutMapping("/{gymId}")
    public ResponseEntity<?> update(@PathVariable UUID gymId, @RequestBody Gym body) {
        Optional<Gym> existingGymOpt = gymRepository.findById(gymId);
        if (existingGymOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Gym not found"));
        }

        Gym gym = existingGymOpt.get();

        if (body.getName() != null && !body.getName().isBlank()) {
            gym.setName(body.getName());
        }

        if (body.getOwner() != null && body.getOwner().getId() != null) {
            Optional<User> ownerOpt = userRepository.findById(body.getOwner().getId());
            if (ownerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "New owner not found"));
            }
            gym.setOwner(ownerOpt.get());
        }

        gymRepository.save(gym);

        return ResponseEntity.ok(gym);
    }

}
