package com.sport.club.repository;

import com.sport.club.model.entity.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, UUID> {
    List<PersonalRecord> findByAthleteIdOrderByAchievedDateDesc(UUID athleteId);

    @Query("SELECT pr FROM PersonalRecord pr WHERE pr.athleteId = :athleteId AND pr.exerciseName = :exerciseName ORDER BY pr.achievedDate DESC")
    List<PersonalRecord> findByAthleteIdAndExerciseName(UUID athleteId, String exerciseName);

    @Query("SELECT pr FROM PersonalRecord pr WHERE pr.athleteId = :athleteId AND pr.exerciseName = :exerciseName AND pr.recordType = :recordType ORDER BY pr.recordValue DESC")
    List<PersonalRecord> findBestRecords(UUID athleteId, String exerciseName, PersonalRecord.RecordType recordType);
}