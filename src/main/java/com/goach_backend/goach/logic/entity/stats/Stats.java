package com.goach_backend.goach.logic.entity.stats;

import com.goach_backend.goach.logic.entity.gym.Gym;
import com.goach_backend.goach.logic.entity.routine.Routine;
import com.goach_backend.goach.logic.entity.user.User;
import com.goach_backend.goach.logic.entity.workout_sessions.WorkoutSession;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Table(name = "Stats")
@Entity
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
    @Column(updatable = false, name = "completed_at")
    private Date completedAt;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;
}
