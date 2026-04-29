package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TrainingPlanResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID athleteId;
    private UUID coachId;
    private String coachName;
    private String sportType;
    private String difficultyLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private boolean isTemplate;
    private int totalItems;
    private int completedItems;
    private double progressPercentage;
    private List<TrainingPlanItemResponse> items;
    private LocalDateTime createdAt;
}