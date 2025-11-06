package com.goach_backend.goach.logic.entity.gym_trio.trainer_trainee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.enums.AssocStatus;
import com.goach_backend.goach.logic.enums.MembershipState;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TrainersTrainees")
public class TrainerTrainee {

    @EmbeddedId
    private TrainerTraineeId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("trainerId")
    @JoinColumn(name = "trainer_id", referencedColumnName = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User trainer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("traineeId")
    @JoinColumn(name = "trainee_id", referencedColumnName = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User trainee;

    @Enumerated(EnumType.STRING)
    @Column(name = "trainee_status")
    private AssocStatus traineeStatus;

    @Column(name = "trainee_payment_date")
    private OffsetDateTime traineePaymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "trainee_payment_status")
    private MembershipState traineePaymentStatus;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private OffsetDateTime updatedAt;

    public TrainerTrainee() {
    }

    public TrainerTrainee(User trainer, User trainee) {
        this.trainer = trainer;
        this.trainee = trainee;
        this.id = new TrainerTraineeId(trainer.getId(), trainee.getId());
    }

    // Getters / Setters
    public TrainerTraineeId getId() {
        return id;
    }

    public void setId(TrainerTraineeId id) {
        this.id = id;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    public User getTrainee() {
        return trainee;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public AssocStatus getTraineeStatus() {
        return traineeStatus;
    }

    public void setTraineeStatus(AssocStatus traineeStatus) {
        this.traineeStatus = traineeStatus;
    }

    public OffsetDateTime getTraineePaymentDate() {
        return traineePaymentDate;
    }

    public void setTraineePaymentDate(OffsetDateTime traineePaymentDate) {
        this.traineePaymentDate = traineePaymentDate;
    }

    public MembershipState getTraineePaymentStatus() {
        return traineePaymentStatus;
    }

    public void setTraineePaymentStatus(MembershipState traineePaymentStatus) {
        this.traineePaymentStatus = traineePaymentStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}