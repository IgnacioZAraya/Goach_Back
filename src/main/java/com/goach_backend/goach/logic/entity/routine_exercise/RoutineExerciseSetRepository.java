package com.goach_backend.goach.logic.entity.routine_exercise;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoutineExerciseSetRepository extends JpaRepository<RoutineExerciseSet, UUID> {
    List<RoutineExerciseSet> findByRoutineExercise_IdOrderBySetNumberAsc(UUID routineExerciseId);
}