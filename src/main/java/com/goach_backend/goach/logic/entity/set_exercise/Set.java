package com.goach_backend.goach.logic.entity.set_exercise;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goach_backend.goach.logic.entity.routine.Routine;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "Set")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Set {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "set_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "routine_id", nullable = false)
    @JsonBackReference // evita loop al serializar
    private Routine routine;

    @Column(name = "set_number")
    private Integer setNumber;

    @Column(name = "work_time")
    private Time workTime;

    @Column(name = "rest_time")
    private Time restTime;

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
