package com.goach_backend.goach.logic.entity.trainee_routine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TraineeRoutineRepository extends JpaRepository<TraineeRoutine, TraineeRoutineId> {
    List<TraineeRoutine> findByIdTraineeId(UUID traineeId);
    List<TraineeRoutine> findByIdRoutineId(UUID routineId);
    boolean existsByIdTraineeIdAndIdRoutineId(UUID traineeId, UUID routineId);
    long deleteByIdTraineeIdAndIdRoutineId(UUID traineeId, UUID routineId);
}
