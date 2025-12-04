package com.goach_backend.goach.logic.entity.set_exercise;

import com.goach_backend.goach.logic.entity.exercise.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetExerciseRepository extends JpaRepository<SetExercise, UUID> {
    List<SetExercise> findBySet_IdOrderByOrderIndexAsc(UUID setId);

    @Query(
            value = """
            SELECT MAX(se.max_weight)
            FROM SetExercise se
            JOIN [Set] s ON s.set_id = se.set_id
            WHERE s.routine_id = :routineId
        """,
            nativeQuery = true
    )
    Double findMaxWeightByRoutineId(@Param("routineId") UUID routineId);
    List<SetExercise> findBySet_Id(UUID setId);
}