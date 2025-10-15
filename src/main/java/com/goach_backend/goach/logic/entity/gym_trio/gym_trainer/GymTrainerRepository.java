package com.goach_backend.goach.logic.entity.gym_trio.gym_trainer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GymTrainerRepository extends JpaRepository<GymTrainer, GymTrainerId> {
    List<GymTrainer> findByGym_Id(UUID gymId);
    List<GymTrainer> findByTrainer_Id(UUID trainerId);
}