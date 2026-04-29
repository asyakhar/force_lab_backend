package com.sport.club.repository;

import com.sport.club.model.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, UUID> {
    List<TrainingPlan> findByAthleteIdOrderByCreatedAtDesc(UUID athleteId);
    List<TrainingPlan> findByCoachIdOrderByCreatedAtDesc(UUID coachId);
    List<TrainingPlan> findByIsTemplateTrue();
    List<TrainingPlan> findByAthleteIdAndStatus(UUID athleteId, TrainingPlan.PlanStatus status);
}