package com.goach_backend.goach.logic.entity.stats;

import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

@Table(name = "Stats")
@Entity
@Getter @Setter
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "stats_id")
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id",nullable = false)
    private WorkoutSession workout;
    @Column(updatable = false, name = "duration_sec")
    private Integer duration;
    @Column(updatable = false)
    private Long calories;
    @Column(name = "actual_rpe")
    private Double actualRpe;

    @Column(name = "actual_rir")
    private Double actualRir;

    @Column(name = "actual_prm")
    private Double actualPrm;
    @Column(updatable = false, name = "completed_at")
    private OffsetDateTime completedAt;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private OffsetDateTime updatedAt;
}
