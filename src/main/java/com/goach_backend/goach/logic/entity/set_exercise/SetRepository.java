package com.goach_backend.goach.logic.entity.set_exercise;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SetRepository extends JpaRepository<Set, UUID> {
    List<Set> findByRoutine_IdOrderBySetNumberAsc(UUID routineId);
}