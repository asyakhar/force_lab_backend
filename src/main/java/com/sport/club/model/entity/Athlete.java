
package com.sport.club.model.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "athletes")
@Data
public class Athlete {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "sport_type")
    private String sportType;

    @Column(name = "rank")
    private String rank; 

    @Column(name = "height_cm")
    private Integer heightCm;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "medical_group")
    private String medicalGroup; 

    @Column(name = "coach_id")
    private UUID coachId;

    @Column(name = "active")
    private Boolean active = true;
}