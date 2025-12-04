package com.goach_backend.goach.logic.entity.workout_sessions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, UUID> {

    List<WorkoutSession> findByTrainee_Email(String email);

    List<WorkoutSession> findByStartedAtBetween(Date start, Date end);


    List<WorkoutSession> findByRoutine_Id(UUID routineId);
}
