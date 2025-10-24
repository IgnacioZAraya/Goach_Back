package com.goach_backend.goach.logic.entity.routine;

import com.goach_backend.goach.logic.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

@Table(name = "Routine")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "routine_id")
    private UUID id;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "trainer_id", referencedColumnName = "user_id", nullable = false)
    private User trainer;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, length = 500)
    private String description;
    @Column(nullable = false, length = 50)
    private String level;
    @Column(nullable = false, name = "total_time")
    private Time totalTime;
    @Column(name = "total_rpe")
    private Integer totalRPE;
    @Column(name = "total_rir")
    private Integer totalRIR;
    @Column(name = "total_prm")
    private Float totalPRM;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "is_active")
    private boolean isActive;
}
