package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TrainingPlanItemResponse {
    private UUID id;
    private String exerciseName;
    private String description;
    private Integer setsCount;
    private Integer repsCount;
    private Double weight;
    private Integer durationMinutes;
    private Double distanceMeters;
    private Integer restSeconds;
    private Integer dayNumber;
    private Integer weekNumber;
    private LocalDate scheduledDate;
    private boolean completed;
    private LocalDateTime completedDate;
    private Double actualValue;
    private String notes;
}