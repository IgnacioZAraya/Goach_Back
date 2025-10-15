package com.goach_backend.goach.logic.entity.gym_trio.gym_trainer;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class GymTrainerId implements Serializable {
    private UUID gymId;
    private UUID trainerId;

    public GymTrainerId() {}
    public GymTrainerId(UUID gymId, UUID trainerId) {
        this.gymId = gymId; this.trainerId = trainerId;
    }

    public UUID getGymId() { return gymId; }
    public void setGymId(UUID gymId) { this.gymId = gymId; }
    public UUID getTrainerId() { return trainerId; }
    public void setTrainerId(UUID trainerId) { this.trainerId = trainerId; }

    @Override public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof GymTrainerId that)) return false;
        return Objects.equals(gymId, that.gymId) && Objects.equals(trainerId, that.trainerId);
    }
    @Override public int hashCode(){ return Objects.hash(gymId, trainerId); }
}