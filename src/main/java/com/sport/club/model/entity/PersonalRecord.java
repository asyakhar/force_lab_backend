package com.sport.club.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personal_records")
@Data
public class PersonalRecord {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "athlete_id", nullable = false)
    private UUID athleteId;

    @Column(name = "exercise_name", nullable = false)
    private String exerciseName;

    @Column(name = "record_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecordType recordType;

    @Column(name = "record_value", nullable = false)
    private Double recordValue;

    @Column(name = "unit", nullable = false)
    private String unit; // kg, sec, meters, reps и т.д.

    @Column(name = "achieved_date", nullable = false)
    private LocalDateTime achievedDate;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum RecordType {
        WEIGHT,      // Вес (жим, присед, тяга)
        TIME,        // Время (бег, плавание)
        DISTANCE,    // Дистанция (прыжки, метания)
        REPS,        // Повторения
        SPEED,       // Скорость
        FLEXIBILITY  // Гибкость
    }
}