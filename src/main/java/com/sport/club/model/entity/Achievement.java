package com.sport.club.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "achievements")
@Data
public class Achievement {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "icon")
    private String icon; // emoji или URL иконки

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AchievementType type;

    @Column(name = "requirement_description")
    private String requirementDescription;

    @Column(name = "requirement_count")
    private Integer requirementCount;

    @Column(name = "points")
    private Integer points;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum AchievementType {
        ATTENDANCE,      // За посещаемость
        RECORD,          // За рекорды
        STREAK,          // За серию тренировок
        COMPETITION,     // За соревнования
        SPECIAL          // Особые достижения
    }
}