package com.goach_backend.goach.logic.entity.exercise;

import com.goach_backend.goach.logic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    @Query("SELECT e FROM Exercise e WHERE LOWER(e.name) LIKE %?1%")
    List<Exercise> findExerciseByName(String character);
}
