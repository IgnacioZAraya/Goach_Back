package com.goach_backend.goach.logic.entity.routine_exercise;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "RoutineExerciseSet")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RoutineExerciseSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "set_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_exercise_id", nullable = false)
    @JsonBackReference // evita loop al serializar
    private RoutineExercise routineExercise;

    @Column(name = "set_number")
    private Integer setNumber;

    @Column(name = "min_reps")
    @Min(0)
    private Integer minReps;

    @Column(name = "max_reps")
    @Min(0)
    private Integer maxReps;

    @Column(name = "target_weight", precision = 10, scale = 2)
    private BigDecimal targetWeight;

    @Column(name = "target_percent_rm")
    private Integer targetPercentRm;

    @Column(name = "rest_sec")
    @Min(0)
    private Integer restSec;

    @Column(name = "target_rpe")
    private Integer targetRpe;

    @Column(name = "target_rir")
    private Integer targetRir;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private OffsetDateTime updatedAt;

    public RoutineExerciseSet() {}

    // getters/setters
    public UUID getId() { return id; }
    public RoutineExercise getRoutineExercise() { return routineExercise; }
    public Integer getSetNumber() { return setNumber; }
    public Integer getMinReps() { return minReps; }
    public Integer getMaxReps() { return maxReps; }
    public BigDecimal getTargetWeight() { return targetWeight; }
    public Integer getTargetPercentRm() { return targetPercentRm; }
    public Integer getRestSec() { return restSec; }
    public Integer getTargetRpe() { return targetRpe; }
    public Integer getTargetRir() { return targetRir; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setI(UUID id) { this.id = id; }
    public void setRoutineExercise(RoutineExercise routineExercise) { this.routineExercise = routineExercise; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }
    public void setMinReps(Integer minReps) { this.minReps = minReps; }
    public void setMaxReps(Integer maxReps) { this.maxReps = maxReps; }
    public void setTargetWeight(BigDecimal targetWeight) { this.targetWeight = targetWeight; }
    public void setTargetPercentRm(Integer targetPercentRm) { this.targetPercentRm = targetPercentRm; }
    public void setRestSec(Integer restSec) { this.restSec = restSec; }
    public void setTargetRpe(Integer targetRpe) { this.targetRpe = targetRpe; }
    public void setTargetRir(Integer targetRir) { this.targetRir = targetRir; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
