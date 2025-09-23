package com.goach_backend.goach.logic.entity.gym;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface GymRepository extends JpaRepository<Gym, UUID> {
    @Query("SELECT g FROM Gym g WHERE LOWER(g.name) LIKE %?1%")
    List<Gym> findGymByName(String character);
}
