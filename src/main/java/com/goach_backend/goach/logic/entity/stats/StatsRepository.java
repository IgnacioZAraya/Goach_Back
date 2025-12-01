package com.goach_backend.goach.logic.entity.stats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface StatsRepository extends JpaRepository<Stats, UUID> {
    List<Stats> findByWorkout_Id(UUID sessionId);

    List<Stats> findByWorkout_Routine_NameAndCompletedAt(String routineName, Date completedAt);
}
