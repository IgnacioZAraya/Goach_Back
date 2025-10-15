package com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrainerTraineeRepository extends JpaRepository<TrainerTrainee, TrainerTraineeId> {
    List<TrainerTrainee> findByTrainer_Id(UUID trainerId);
    List<TrainerTrainee> findByTrainee_Id(UUID traineeId);
}