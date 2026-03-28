
package com.sport.club.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "trainings")
@Data
public class Training {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "training_date", nullable = false)
    private LocalDateTime trainingDate;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "location")
    private String location;

    @Column(name = "coach_id", nullable = false)
    private UUID coachId;

    @Column(name = "sport_type")
    private String sportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_type")
    private TrainingType trainingType;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum TrainingType {
        ОФП, СФП, ТЕХНИЧЕСКАЯ, ТАКТИЧЕСКАЯ, ТЕОРЕТИЧЕСКАЯ, ВОССТАНОВИТЕЛЬНАЯ
    }
}