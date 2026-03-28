package com.sport.club.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "training_attendance")
@Data
public class TrainingAttendance {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "training_id")
    private UUID trainingId;

    @Column(name = "athlete_id")
    private UUID athleteId;

    private String status; // PRESENT, ABSENT, LATE, EXCUSED

    private String notes;

    @Column(name = "marked_at")
    private LocalDateTime markedAt;
}