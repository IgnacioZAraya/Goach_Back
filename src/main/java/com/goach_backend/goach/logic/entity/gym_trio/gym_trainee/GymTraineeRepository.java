package com.goach_backend.goach.logic.entity.gym_trio.gym_trainee;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GymTraineeRepository extends JpaRepository<GymTrainee, GymTraineeId> {
    List<GymTrainee> findByGym_Id(UUID gymId);
    List<GymTrainee> findByTrainee_Id(UUID traineeId);
}