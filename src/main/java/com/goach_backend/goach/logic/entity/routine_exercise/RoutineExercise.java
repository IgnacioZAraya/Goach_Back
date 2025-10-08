package com.goach_backend.goach.logic.entity.routine_exercise;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goach_backend.goach.logic.entity.exercise.Exercise;
import com.goach_backend.goach.logic.entity.routine.Routine;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "RoutineExercise")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RoutineExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "routine_exercise_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", referencedColumnName = "routine_id", nullable = false)
    @JsonBackReference // la rutina, si la serializas, no vuelve a bajar a sus ejercicios
    private Routine routine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", nullable = false)
    private Exercise exercise;

    // "order" suele ser reservada; en SQL Server va con []
    @Column(name = "[order]")
    private Integer orderIndex;

    @Column(name = "default_rest_sec")
    @Min(value = 0, message = "El descanso no puede ser negativo")
    private Integer defaultRestSec;

    @Column(name = "target_rpe")
    private Integer targetRpe;

    @Column(name = "target_rir")
    private Integer targetRir;

    @Column(name = "tempo")
    private String tempo;

    @Column(name = "block")
    private String block;

    @Column(name = "superset_group")
    private String supersetGroup;

    @OneToMany(mappedBy = "routineExercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("setNumber ASC")
    @JsonManagedReference // expone los sets y evita el loop
    private List<RoutineExerciseSet> sets = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private OffsetDateTime updatedAt;

    public RoutineExercise() {}

    // helpers
    public void addSet(RoutineExerciseSet set) {
        set.setRoutineExercise(this);
        this.sets.add(set);
    }
    public void removeSet(RoutineExerciseSet set) {
        set.setRoutineExercise(null);
        this.sets.remove(set);
    }

    // getters/setters
    public Long getId() { return id; }
    public Routine getRoutine() { return routine; }
    public Exercise getExercise() { return exercise; }
    public Integer getOrderIndex() { return orderIndex; }
    public Integer getDefaultRestSec() { return defaultRestSec; }
    public Integer getTargetRpe() { return targetRpe; }
    public Integer getTargetRir() { return targetRir; }
    public String getTempo() { return tempo; }
    public String getBlock() { return block; }
    public String getSupersetGroup() { return supersetGroup; }
    public List<RoutineExerciseSet> getSets() { return sets; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setRoutine(Routine routine) { this.routine = routine; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    public void setDefaultRestSec(Integer defaultRestSec) { this.defaultRestSec = defaultRestSec; }
    public void setTargetRpe(Integer targetRpe) { this.targetRpe = targetRpe; }
    public void setTargetRir(Integer targetRir) { this.targetRir = targetRir; }
    public void setTempo(String tempo) { this.tempo = tempo; }
    public void setBlock(String block) { this.block = block; }
    public void setSupersetGroup(String supersetGroup) { this.supersetGroup = supersetGroup; }
    public void setSets(List<RoutineExerciseSet> sets) { this.sets = sets; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
