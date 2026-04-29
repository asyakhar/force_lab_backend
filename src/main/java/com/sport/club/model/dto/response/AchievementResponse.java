package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AchievementResponse {
    private UUID id;
    private String name;
    private String description;
    private String icon;
    private String type;
    private String requirementDescription;
    private Integer requirementCount;
    private Integer points;
    private boolean earned;
    private LocalDateTime earnedAt;
    private Integer progress;
}