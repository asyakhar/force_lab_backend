package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProgressStatsResponse {
    private int totalTrainings;
    private int currentStreak;
    private int longestStreak;
    private double attendanceRate;
    private List<PersonalRecordResponse> recentRecords;
    private Map<String, List<ProgressDataPoint>> progressByExercise;

    @Data
    @Builder
    public static class ProgressDataPoint {
        private String date;
        private Double value;
        private String label;
    }
}