package com.goach_backend.goach.logic.entity.set_exercise;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.stats.Stats;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "[Set]")
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
    @JoinColumn(name = "routine_id", referencedColumnName = "routine_id", nullable = false)
    @JsonBackReference(value = "routine-sets")
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
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    @PastOrPresent
    private Date updatedAt;
}
