package com.sport.club.model.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTrainingRequest {
    private String title;
    private String description;
    private LocalDateTime trainingDate;
    private Integer durationMinutes;
    private String location;
    private String sportType;
    private Integer maxParticipants;
}