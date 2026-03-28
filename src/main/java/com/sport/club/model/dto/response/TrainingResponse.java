package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrainingResponse {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime trainingDate;
    private Integer durationMinutes;
    private String location;
    private String sportType;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String coachName;
    private UUID coachId;
}