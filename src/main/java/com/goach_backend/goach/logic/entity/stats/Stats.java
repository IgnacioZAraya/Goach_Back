package com.goach_backend.goach.logic.entity.stats;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.set_exercise.Set;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Table(name = "Stats")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "stats_id")
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", nullable = false)
    private WorkoutSession workout;
    @Column(updatable = false, name = "duration")
    private String duration;
    @Column(updatable = false)
    private Long calories;
    @Min(value = 0, message = "El RPE no puede ser negativo.")
    @Max(value = 10, message = "El RPE no puede ser mayor a 10")
    @Column(name = "actual_rpe")
    private Integer actualRPE;
    @Column(name = "actual_rir")
    private Integer actualRIR;
    @Column(name = "actual_prm")
    private Float actualPRM;
    @Column(updatable = false, name = "completed_at")
    private OffsetDateTime completedAt;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
