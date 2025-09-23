package com.goach_backend.goach.logic.entity.workout_sessions;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface WorkoutSessionInterface extends JpaRepository<WorkoutSession, UUID> {
    @Query("SELECT wss FROM WorkoutSession wss WHERE LOWER(wss.trainee) LIKE %?1%")
    List<WorkoutSession> findWorkoutSessionByTrainee(String character);
    @Query("SELECT wss FROM WorkoutSession wss WHERE wss.startedAt BETWEEN :startedAt AND :finishedAt")
    List<WorkoutSession> findWorkoutSessionByStartedAtBetweenAndFinishedAt(@Param("startedAt") Date startedAt, @Param("finishedAt") Date finishedAt);
}
