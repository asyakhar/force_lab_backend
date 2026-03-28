
package com.sport.club.model.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StatisticsResponse {
    private AthleteStats athleteStats;
    private TrainingStats trainingStats;
    private CompetitionStats competitionStats;
    private HealthStats healthStats;

    @Data
    @Builder
    public static class AthleteStats {
        private Integer totalAthletes;
        private Integer activeAthletes;
        private Integer athletesBySport;
        private Integer rankDistribution;
    }

    @Data
    @Builder
    public static class TrainingStats {
        private Integer totalTrainings;
        private Double attendanceRate;
        private Integer trainingsThisMonth;
        private Integer averageParticipants;
    }

    @Data
    @Builder
    public static class CompetitionStats {
        private Integer totalCompetitions;
        private Integer medalsCount;
        private Integer goldMedals;
        private Integer silverMedals;
        private Integer bronzeMedals;
    }

    @Data
    @Builder
    public static class HealthStats {
        private Integer activeInjuries;
        private Integer recoveredInjuries;
        private Double averageRecoveryDays;
    }
}