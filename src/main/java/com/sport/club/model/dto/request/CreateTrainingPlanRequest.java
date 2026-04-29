package com.sport.club.model.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateTrainingPlanRequest {
    private String name;
    private String description;
    private UUID athleteId;
    private String sportType;
    private String difficultyLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isTemplate;
    private List<PlanItemRequest> items;

    @Data
    public static class PlanItemRequest {
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
    }
}