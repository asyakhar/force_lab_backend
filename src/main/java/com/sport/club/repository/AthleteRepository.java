package com.sport.club.repository;

import com.sport.club.model.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, UUID> {
    Optional<Athlete> findByUserId(UUID userId);
    List<Athlete> findAll();
    List<Athlete> findByCoachId(UUID coachId);
}