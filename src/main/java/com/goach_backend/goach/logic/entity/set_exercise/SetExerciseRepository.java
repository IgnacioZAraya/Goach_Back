package com.goach_backend.goach.logic.entity.set_exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SetExerciseRepository extends JpaRepository<SetExercise, UUID> {
    List<SetExercise> findBySet_IdOrderByOrderIndexAsc(UUID setId);
}