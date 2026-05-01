package com.sport.club.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "athlete_achievements")
@Data
public class AthleteAchievement {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "athlete_id", nullable = false)
    private UUID athleteId;

    @Column(name = "achievement_id", nullable = false)
    private UUID achievementId;

    @Column(name = "earned_at", nullable = false)
    private LocalDateTime earnedAt;

    @Column(name = "progress")
    private Integer progress;

    @Column(name = "completed")
    private Boolean completed = false;

    @PrePersist
    protected void onCreate() {
        earnedAt = LocalDateTime.now();
    }
}