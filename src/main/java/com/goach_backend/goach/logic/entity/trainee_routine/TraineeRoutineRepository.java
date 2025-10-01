package com.goach_backend.goach.logic.entity.trainee_routine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TraineeRoutineRepository extends JpaRepository<TraineeRoutine, TraineeRoutineId> {
    List<TraineeRoutine> findByIdTraineeId(Long traineeId);
    List<TraineeRoutine> findByIdRoutineId(Long routineId);
    boolean existsByIdTraineeIdAndIdRoutineId(Long traineeId, Long routineId);
    long deleteByIdTraineeIdAndIdRoutineId(Long traineeId, Long routineId);
}
