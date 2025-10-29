package com.goach_backend.goach.logic.entity.exercise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.goach_backend.goach.logic.entity.muscle_group.MuscleGroupEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Table(name = "Exercise")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "exercise_id")
    private UUID id;
    @Column(nullable = false)
    private String name;
    @Column(name = "muscle_group", nullable = false)
    @Enumerated(EnumType.STRING)
    private MuscleGroupEnum muscleGroup;
    @Column(nullable = false, length = 500)
    private String description;
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
    @Column(name = "is_active")
    private boolean isActive;

}
