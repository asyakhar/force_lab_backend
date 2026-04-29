package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PersonalRecordResponse {
    private UUID id;
    private String exerciseName;
    private String recordType;
    private Double recordValue;
    private String unit;
    private LocalDateTime achievedDate;
    private String notes;
    private boolean isCurrentRecord;
}