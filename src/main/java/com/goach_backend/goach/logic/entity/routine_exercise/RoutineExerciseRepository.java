package com.goach_backend.goach.logic.entity.routine_exercise;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, UUID> {
    List<RoutineExercise> findByRoutine_IdOrderByOrderIndexAsc(UUID routineId);
}