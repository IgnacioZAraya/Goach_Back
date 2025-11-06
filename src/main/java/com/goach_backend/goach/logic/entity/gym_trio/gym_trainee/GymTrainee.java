package com.goach_backend.goach.logic.entity.gym_trio.gym_trainee;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.enums.AssocStatus;
import com.goach_backend.goach.logic.enums.MembershipState;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Table(name = "GymTrainees")
public class GymTrainee {

    @EmbeddedId
    @NotNull
    private GymTraineeId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("gymId")
    @JoinColumn(name = "gym_id", referencedColumnName = "gym_id", nullable = false)
    @JsonBackReference
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("traineeId")
    @JoinColumn(name = "trainee_id", referencedColumnName = "user_id", nullable = false)
    private User trainee;

    @Enumerated(EnumType.STRING)
    @Column(name = "associate_status")
    private AssocStatus associateStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_status")
    private MembershipState membershipStatus;

    @Column(name = "membership_price", precision = 12, scale = 2)
    private BigDecimal membershipPrice;


    @Column(name = "membership_date")
    private Date membershipDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private OffsetDateTime updatedAt;

    public GymTrainee() {
    }

    public GymTrainee(Gym gym, User trainee) {
        this.gym = gym;
        this.trainee = trainee;
        this.id = new GymTraineeId(gym.getId(), trainee.getId());
    }

    public GymTraineeId getId() {
        return id;
    }

    public void setId(GymTraineeId id) {
        this.id = id;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public User getTrainee() {
        return trainee;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public AssocStatus getAssociateStatus() {
        return associateStatus;
    }

    public void setAssociateStatus(AssocStatus associateStatus) {
        this.associateStatus = associateStatus;
    }

    public MembershipState getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipState membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public BigDecimal getMembershipPrice() {
        return membershipPrice;
    }

    public void setMembershipPrice(BigDecimal membershipPrice) {
        this.membershipPrice = membershipPrice;
    }

    public Date getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(Date membershipDate) {
        this.membershipDate = membershipDate;
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