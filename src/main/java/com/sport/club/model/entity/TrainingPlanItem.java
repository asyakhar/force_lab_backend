package com.sport.club.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "training_plan_items")
@Data
public class TrainingPlanItem {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "exercise_name", nullable = false)
    private String exerciseName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sets_count")
    private Integer setsCount;

    @Column(name = "reps_count")
    private Integer repsCount;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "distance_meters")
    private Double distanceMeters;

    @Column(name = "rest_seconds")
    private Integer restSeconds;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "completed")
    private Boolean completed = false;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "actual_value")
    private Double actualValue;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}