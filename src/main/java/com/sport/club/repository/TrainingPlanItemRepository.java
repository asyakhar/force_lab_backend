package com.sport.club.repository;

import com.sport.club.model.entity.TrainingPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingPlanItemRepository extends JpaRepository<TrainingPlanItem, UUID> {
    List<TrainingPlanItem> findByPlanIdOrderByDayNumberAsc(UUID planId);

    @Query("SELECT tpi FROM TrainingPlanItem tpi WHERE tpi.planId IN :planIds AND tpi.scheduledDate = :date")
    List<TrainingPlanItem> findTodayItems(List<UUID> planIds, LocalDate date);

    long countByPlanIdAndCompleted(UUID planId, boolean completed);
}