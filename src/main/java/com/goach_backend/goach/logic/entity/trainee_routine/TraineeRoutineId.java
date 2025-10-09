package com.goach_backend.goach.logic.entity.trainee_routine;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TraineeRoutineId implements Serializable {

    @Column(name = "trainee_id", nullable = false)
    private UUID traineeId;

    @Column(name = "routine_id", nullable = false)
    private UUID routineId;

    public TraineeRoutineId() {}

    public TraineeRoutineId(UUID traineeId, UUID routineId) {
        this.traineeId = traineeId;
        this.routineId = routineId;
    }

    public UUID getTraineeId() { return traineeId; }
    public UUID getRoutineId() { return routineId; }

    public void setTraineeId(UUID traineeId) { this.traineeId = traineeId; }
    public void setRoutineId(UUID routineId) { this.routineId = routineId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TraineeRoutineId that)) return false;
        return Objects.equals(traineeId, that.traineeId) &&
                Objects.equals(routineId, that.routineId);
    }
    @Override public int hashCode() {
        return Objects.hash(traineeId, routineId);
    }
}

