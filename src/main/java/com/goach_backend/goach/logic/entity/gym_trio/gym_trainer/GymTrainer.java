package com.goach_backend.goach.logic.entity.gym_trio.gym_trainer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.enums.AssocStatus;
import com.goach_backend.goach.logic.enums.MembershipState;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "GymTrainers")
public class GymTrainer {

    @EmbeddedId
    private GymTrainerId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("gymId")
    @JoinColumn(name = "gym_id", referencedColumnName = "gym_id", nullable = false)
    @JsonBackReference
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("trainerId")
    @JoinColumn(name = "trainer_id", referencedColumnName = "user_id", nullable = false)
    private User trainer;

    @Enumerated(EnumType.STRING)
    @Column(name = "associate_status")
    private AssocStatus associateStatus;

    @Column(name = "gym_payment_date")
    private OffsetDateTime gymPaymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gym_payment_status")
    private MembershipState gymPaymentStatus;

    @Column(name = "gym_payment_price", precision = 12, scale = 2)
    private BigDecimal gymPaymentPrice;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private OffsetDateTime updatedAt;

    public GymTrainer() {
    }

    public GymTrainer(Gym gym, User trainer) {
        this.gym = gym;
        this.trainer = trainer;
        this.id = new GymTrainerId(gym.getId(), trainer.getId());
    }

    public GymTrainerId getId() {
        return id;
    }

    public void setId(GymTrainerId id) {
        this.id = id;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public User getTrainer() {
        return trainer;
    }

    public void setTrainer(User trainer) {
        this.trainer = trainer;
    }

    public AssocStatus getAssociateStatus() {
        return associateStatus;
    }

    public void setAssociateStatus(AssocStatus associateStatus) {
        this.associateStatus = associateStatus;
    }

    public OffsetDateTime getGymPaymentDate() {
        return gymPaymentDate;
    }

    public void setGymPaymentDate(OffsetDateTime gymPaymentDate) {
        this.gymPaymentDate = gymPaymentDate;
    }

    public MembershipState getGymPaymentStatus() {
        return gymPaymentStatus;
    }

    public void setGymPaymentStatus(MembershipState gymPaymentStatus) {
        this.gymPaymentStatus = gymPaymentStatus;
    }

    public BigDecimal getGymPaymentPrice() {
        return gymPaymentPrice;
    }

    public void setGymPaymentPrice(BigDecimal gymPaymentPrice) {
        this.gymPaymentPrice = gymPaymentPrice;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}