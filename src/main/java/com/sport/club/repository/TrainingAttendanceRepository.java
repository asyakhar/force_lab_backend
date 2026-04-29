package com.sport.club.repository;

import com.sport.club.model.entity.TrainingAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrainingAttendanceRepository extends JpaRepository<TrainingAttendance, UUID> {
    List<TrainingAttendance> findByTrainingId(UUID trainingId);


    List<TrainingAttendance> findByAthleteId(UUID athleteId);

    Optional<TrainingAttendance> findByTrainingIdAndAthleteId(UUID trainingId, UUID athleteId);
    long countByTrainingIdAndStatus(UUID trainingId, String status);
}