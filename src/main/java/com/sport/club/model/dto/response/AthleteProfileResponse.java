
package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AthleteProfileResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private String sportType;
    private String rank;
    private Integer heightCm;
    private Double weightKg;
    private String medicalGroup;
    private String coachName;
    private UUID coachId;
    private Integer age;
    private String status;
}