package com.goach_backend.goach.logic.entity.trainee_routine;

import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "TraineeRoutines")
public class TraineeRoutine {

    @EmbeddedId
    @NotNull(message = "El identificador compuesto no puede ser nulo")
    private TraineeRoutineId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("traineeId")
    @JoinColumn(name = "trainee_id", referencedColumnName = "user_id", nullable = false)
    @NotNull(message = "El trainee es obligatorio")
    private User trainee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("routineId")
    @JoinColumn(name = "routine_id", referencedColumnName = "routine_id", nullable = false)
    @NotNull(message = "La rutina es obligatoria")
    private Routine routine;

    @Column(name = "assigned_at", nullable = false)
    @NotNull(message = "La fecha de asignación no puede ser nula")
    @PastOrPresent(message = "La fecha de asignación no puede estar en el futuro")
    private OffsetDateTime assignedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent(message = "La fecha de creación debe ser pasada o presente")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent(message = "La fecha de actualización debe ser pasada o presente")
    private OffsetDateTime updatedAt;

    public TraineeRoutine() {}

    public TraineeRoutine(User trainee, Routine routine, OffsetDateTime assignedAt) {
        this.trainee = trainee;
        this.routine = routine;
        this.id = new TraineeRoutineId(trainee.getId(), routine.getId());
        this.assignedAt = assignedAt;
    }

    public TraineeRoutineId getId() { return id; }
    public User getTrainee() { return trainee; }
    public Routine getRoutine() { return routine; }
    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setId(TraineeRoutineId id) {
        this.id = id;
    }

    public void setTrainee(User trainee) {
        this.trainee = trainee;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }
}