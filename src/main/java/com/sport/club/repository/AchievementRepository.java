package com.sport.club.repository;

import com.sport.club.model.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
    List<Achievement> findByType(Achievement.AchievementType type);
}