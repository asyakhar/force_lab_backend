
package com.sport.club.model.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private LocalDate birthDate;
    private String sportType;
    private String rank;
    private Integer heightCm;
    private Double weightKg;
    private UUID coachId;
    private String role;
}