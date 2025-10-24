package com.goach_backend.goach.logic.entity.set_exercise;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goach_backend.goach.logic.entity.exercise.Exercise;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "SetExercise")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SetExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "set_exercise_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "set_id", referencedColumnName = "set_id", nullable = false)
    @JsonBackReference // el set, si la serializas, no vuelve a bajar a sus ejercicios
    private Set set;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", referencedColumnName = "exercise_id", nullable = false)
    private Exercise exercise;

    // "order" suele ser reservada; en SQL Server va con []
    @Column(name = "[order]")
    private Integer orderIndex;

    @Column(name = "duration")
    private Time duration;

    @Column(name = "max_reps")
    @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
    private Integer maxReps;

    @Column(name = "min_reps")
    @Min(value = 0, message = "Las repeticiones no pueden ser negativas")
    private Integer minReps;

    @Column(name = "max_weight")
    @Min(value = 0, message = "El peso no puede ser negativo")
    private Float maxWeight;

    @Column(name = "min_weight")
    @Min(value = 0, message = "El peso no puede ser negativo")
    private Float minWeight;

    @Column(name = "target_rpe")
    private Integer targetRPE;

    @Column(name = "target_rir")
    private Integer targetRIR;

    @Column(name = "target_prm")
    private Float targetPRM;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    @PastOrPresent
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private OffsetDateTime updatedAt;
}
