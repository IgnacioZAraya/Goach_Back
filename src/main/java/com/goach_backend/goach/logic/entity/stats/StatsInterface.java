package com.goach_backend.goach.logic.entity.stats;

import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface StatsInterface extends JpaRepository<Stats, UUID> {
    @Query("SELECT stts FROM Stats stts WHERE LOWER(stts.workout) LIKE %?1%")
    List<Stats> finStatsByWorkout(String character);
    @Query("SELECT stts FROM Stats stts JOIN FETCH stts.workout wss WHERE wss.routine.name LIKE %:routineName% AND wss.finishedAt = :statsCompletion")
    List<Stats> findStatsByWorkout_RoutineAndWorkoutAndCompletedAt(@Param("routineName") String routineName, @Param("statsCompletion") Date completedAt);
}
