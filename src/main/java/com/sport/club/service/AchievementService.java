package com.sport.club.service;

import com.sport.club.model.dto.response.AchievementResponse;
import com.sport.club.model.entity.Achievement;
import com.sport.club.model.entity.AthleteAchievement;
import com.sport.club.repository.AchievementRepository;
import com.sport.club.repository.AthleteAchievementRepository;
import com.sport.club.repository.TrainingAttendanceRepository;
import com.sport.club.repository.PersonalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final AthleteAchievementRepository athleteAchievementRepository;
    private final TrainingAttendanceRepository attendanceRepository;
    private final PersonalRecordRepository personalRecordRepository;

    @Transactional(readOnly = true)
    public List<AchievementResponse> getAthleteAchievements(UUID athleteId) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        List<AthleteAchievement> earnedAchievements = athleteAchievementRepository.findByAthleteId(athleteId);

        return allAchievements.stream()
                .map(achievement -> {
                    AthleteAchievement earned = earnedAchievements.stream()
                            .filter(ea -> ea.getAchievementId().equals(achievement.getId()))
                            .findFirst()
                            .orElse(null);

                    return AchievementResponse.builder()
                            .id(achievement.getId())
                            .name(achievement.getName())
                            .description(achievement.getDescription())
                            .icon(achievement.getIcon())
                            .type(achievement.getType().name())
                            .requirementDescription(achievement.getRequirementDescription())
                            .requirementCount(achievement.getRequirementCount())
                            .points(achievement.getPoints())
                            .earned(earned != null && earned.getCompleted())
                            .earnedAt(earned != null ? earned.getEarnedAt() : null)
                            .progress(earned != null ? earned.getProgress() : 0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void checkAndAwardAchievements(UUID athleteId) {
        // Проверка достижений за посещаемость
        long attendanceCount = attendanceRepository.countByTrainingIdAndStatus(athleteId, "PRESENT");
        checkAttendanceAchievements(athleteId, (int) attendanceCount);

        // Проверка достижений за рекорды
        long recordsCount = personalRecordRepository.findByAthleteIdOrderByAchievedDateDesc(athleteId).size();
        checkRecordAchievements(athleteId, (int) recordsCount);
    }

    private void checkAttendanceAchievements(UUID athleteId, int count) {
        List<Achievement> attendanceAchievements = achievementRepository.findByType(Achievement.AchievementType.ATTENDANCE);

        for (Achievement achievement : attendanceAchievements) {
            if (!athleteAchievementRepository.existsByAthleteIdAndAchievementId(athleteId, achievement.getId())) {
                AthleteAchievement athleteAchievement = new AthleteAchievement();
                athleteAchievement.setAthleteId(athleteId);
                athleteAchievement.setAchievementId(achievement.getId());
                athleteAchievement.setProgress(count);

                if (count >= achievement.getRequirementCount()) {
                    athleteAchievement.setCompleted(true);
                    athleteAchievement.setEarnedAt(LocalDateTime.now());
                }

                athleteAchievementRepository.save(athleteAchievement);
            }
        }
    }

    private void checkRecordAchievements(UUID athleteId, int count) {
        List<Achievement> recordAchievements = achievementRepository.findByType(Achievement.AchievementType.RECORD);

        for (Achievement achievement : recordAchievements) {
            if (!athleteAchievementRepository.existsByAthleteIdAndAchievementId(athleteId, achievement.getId())) {
                AthleteAchievement athleteAchievement = new AthleteAchievement();
                athleteAchievement.setAthleteId(athleteId);
                athleteAchievement.setAchievementId(achievement.getId());
                athleteAchievement.setProgress(count);

                if (count >= achievement.getRequirementCount()) {
                    athleteAchievement.setCompleted(true);
                    athleteAchievement.setEarnedAt(LocalDateTime.now());
                }

                athleteAchievementRepository.save(athleteAchievement);
            }
        }
    }
}