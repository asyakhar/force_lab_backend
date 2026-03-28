package com.sport.club.repository;

import com.sport.club.model.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TrainingRepository extends JpaRepository<Training, UUID> {
    List<Training> findByCoachId(UUID coachId);

    @Query("SELECT t FROM Training t WHERE t.trainingDate > :now ORDER BY t.trainingDate ASC")
    List<Training> findUpcomingTrainings(LocalDateTime now);
}