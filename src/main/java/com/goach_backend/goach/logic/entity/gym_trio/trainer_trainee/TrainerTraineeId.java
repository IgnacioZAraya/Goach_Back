package com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TrainerTraineeId implements Serializable {
    private UUID trainerId;
    private UUID traineeId;

    public TrainerTraineeId() {}
    public TrainerTraineeId(UUID trainerId, UUID traineeId) {
        this.trainerId = trainerId;
        this.traineeId = traineeId;
    }

    public UUID getTrainerId() { return trainerId; }
    public void setTrainerId(UUID trainerId) { this.trainerId = trainerId; }
    public UUID getTraineeId() { return traineeId; }
    public void setTraineeId(UUID traineeId) { this.traineeId = traineeId; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainerTraineeId that)) return false;
        return Objects.equals(trainerId, that.trainerId) &&
                Objects.equals(traineeId, that.traineeId);
    }
    @Override public int hashCode() { return Objects.hash(trainerId, traineeId); }
}