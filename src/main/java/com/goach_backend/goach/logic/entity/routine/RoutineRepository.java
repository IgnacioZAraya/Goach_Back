package com.goach_backend.goach.logic.entity.routine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoutineRepository extends JpaRepository<Routine, UUID> {
    @Query("SELECT r FROM Routine r WHERE LOWER(r.name) LIKE %?1%")
    List<Routine> findRoutineByName(String character);
}
