package com.goach_backend.goach.logic.entity.workout_sessions;

import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.user.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Table(name = "WorkoutSession")
@Entity
public class WorkoutSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "session_id")
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trainee_id", referencedColumnName = "user_id",nullable = false)
    private User trainee;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "routine_id", referencedColumnName = "routine_id",nullable = false)
    private Routine routine;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "gym_id", referencedColumnName = "gym_id")
    private Gym gym;
    @Column(updatable = false, name = "started_at")
    private Date startedAt;
    @Column(updatable = false, name="finished_at")
    private Date finishedAt;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;

    public WorkoutSession() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getTrainee() {
        return trainee;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Gym getGym() {
        return gym;
    }

    public void setGym(Gym gym) {
        this.gym = gym;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
