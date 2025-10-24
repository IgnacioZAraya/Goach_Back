package com.goach_backend.goach.logic.entity.gym_trio.gym_trainee;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class GymTraineeId implements Serializable {
    private UUID gymId;
    private UUID traineeId;

    public GymTraineeId() {}
    public GymTraineeId(UUID gymId, UUID traineeId) {
        this.gymId = gymId;
        this.traineeId = traineeId;
    }

    public UUID getGymId() { return gymId; }
    public void setGymId(UUID gymId) { this.gymId = gymId; }

    public UUID getTraineeId() { return traineeId; }
    public void setTraineeId(UUID traineeId) { this.traineeId = traineeId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GymTraineeId that)) return false;
        return Objects.equals(gymId, that.gymId) && Objects.equals(traineeId, that.traineeId);
    }
    @Override public int hashCode() { return Objects.hash(gymId, traineeId); }
}