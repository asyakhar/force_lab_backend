package com.sport.club.repository;

import com.sport.club.model.entity.AthleteAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AthleteAchievementRepository extends JpaRepository<AthleteAchievement, UUID> {
    List<AthleteAchievement> findByAthleteId(UUID athleteId);
    boolean existsByAthleteIdAndAchievementId(UUID athleteId, UUID achievementId);
    Optional<AthleteAchievement> findByAthleteIdAndAchievementId(UUID athleteId, UUID achievementId);
}