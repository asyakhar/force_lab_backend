package com.sport.club.service;

import com.sport.club.model.dto.response.PersonalRecordResponse;
import com.sport.club.model.dto.response.ProgressStatsResponse;
import com.sport.club.model.entity.PersonalRecord;
import com.sport.club.model.entity.Training;
import com.sport.club.model.entity.TrainingAttendance;
import com.sport.club.repository.PersonalRecordRepository;
import com.sport.club.repository.TrainingAttendanceRepository;
import com.sport.club.repository.TrainingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final PersonalRecordRepository personalRecordRepository;
    private final TrainingAttendanceRepository attendanceRepository;
    private final TrainingRepository trainingRepository;

    @Transactional(readOnly = true)
    public ProgressStatsResponse getProgressStats(UUID athleteId) {
        List<PersonalRecord> allRecords = personalRecordRepository
                .findByAthleteIdOrderByAchievedDateDesc(athleteId);

        // Получаем все записи о посещении тренировок
        List<TrainingAttendance> attendances = attendanceRepository.findByAthleteId(athleteId);

        // Вычисляем реальные показатели
        int totalTrainings = attendances.size();
        int currentStreak = calculateCurrentStreak(attendances);
        int longestStreak = calculateLongestStreak(attendances);
        double attendanceRate = calculateAttendanceRate(attendances);

        // Группируем рекорды по упражнениям для графиков
        Map<String, List<ProgressStatsResponse.ProgressDataPoint>> progressByExercise = new HashMap<>();

        if (allRecords != null && !allRecords.isEmpty()) {
            Map<String, List<PersonalRecord>> groupedByExercise = allRecords.stream()
                    .collect(Collectors.groupingBy(PersonalRecord::getExerciseName));

            for (Map.Entry<String, List<PersonalRecord>> entry : groupedByExercise.entrySet()) {
                List<ProgressStatsResponse.ProgressDataPoint> dataPoints = entry.getValue().stream()
                        .sorted(Comparator.comparing(PersonalRecord::getAchievedDate))
                        .map(record -> ProgressStatsResponse.ProgressDataPoint.builder()
                                .date(record.getAchievedDate().toLocalDate().toString())
                                .value(record.getRecordValue())
                                .label(record.getUnit())
                                .build())
                        .collect(Collectors.toList());

                progressByExercise.put(entry.getKey(), dataPoints);
            }
        }

        return ProgressStatsResponse.builder()
                .totalTrainings(totalTrainings)
                .currentStreak(currentStreak)
                .longestStreak(longestStreak)
                .attendanceRate(attendanceRate)
                .recentRecords(allRecords != null ?
                        allRecords.stream()
                                .limit(5)
                                .map(this::mapToRecordResponse)
                                .collect(Collectors.toList()) :
                        Collections.emptyList())
                .progressByExercise(progressByExercise)
                .build();
    }

    @Transactional
    public PersonalRecordResponse addPersonalRecord(UUID athleteId, String exerciseName,
                                                    String recordType, Double value,
                                                    String unit, String notes) {
        PersonalRecord record = new PersonalRecord();
        record.setAthleteId(athleteId);
        record.setExerciseName(exerciseName);
        record.setRecordType(PersonalRecord.RecordType.valueOf(recordType));
        record.setRecordValue(value);
        record.setUnit(unit);
        record.setAchievedDate(LocalDateTime.now());
        record.setNotes(notes);

        PersonalRecord saved = personalRecordRepository.save(record);
        return mapToRecordResponse(saved);
    }

    /**
     * Расчет процента посещаемости
     * Для нового пользователя с 0 тренировок возвращает 0%
     */
    private double calculateAttendanceRate(List<TrainingAttendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return 0.0; // Нет тренировок - посещаемость 0%
        }

        // Считаем все тренировки, на которые был записан спортсмен
        long totalRegistered = attendances.size();

        // Считаем тренировки, где статус "PRESENT" (присутствовал)
        long attended = attendances.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
                .count();

        if (totalRegistered == 0) {
            return 0.0;
        }

        // Вычисляем процент
        return (double) attended / totalRegistered * 100.0;
    }

    /**
     * Расчет текущей серии (streak) - количество дней подряд с тренировками
     */
    private int calculateCurrentStreak(List<TrainingAttendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return 0; // Нет тренировок - стрик 0
        }

        // Получаем только посещенные тренировки
        Set<LocalDate> attendedDates = attendances.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
                .map(a -> {
                    // Получаем дату тренировки из Training
                    Optional<Training> training = trainingRepository.findById(a.getTrainingId());
                    return training.map(t -> t.getTrainingDate().toLocalDate()).orElse(null);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (attendedDates.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // Проверяем, была ли тренировка сегодня или вчера
        LocalDate lastTrainingDate = attendedDates.iterator().next();
        if (!lastTrainingDate.equals(today) && !lastTrainingDate.equals(yesterday)) {
            return 0; // Стрик прерван
        }

        int streak = 1;
        LocalDate currentDate = lastTrainingDate;

        for (LocalDate date : attendedDates) {
            if (date.equals(lastTrainingDate)) continue; // Пропускаем первую дату

            if (ChronoUnit.DAYS.between(date, currentDate) == 1) {
                streak++;
                currentDate = date;
            } else {
                break;
            }
        }

        return streak;
    }

    /**
     * Расчет самой длинной серии тренировок
     */
    private int calculateLongestStreak(List<TrainingAttendance> attendances) {
        if (attendances == null || attendances.isEmpty()) {
            return 0;
        }

        // Получаем только посещенные тренировки, отсортированные по дате
        List<LocalDate> attendedDates = attendances.stream()
                .filter(a -> "PRESENT".equals(a.getStatus()) || "LATE".equals(a.getStatus()))
                .map(a -> {
                    Optional<Training> training = trainingRepository.findById(a.getTrainingId());
                    return training.map(t -> t.getTrainingDate().toLocalDate()).orElse(null);
                })
                .filter(Objects::nonNull)
                .sorted()
                .distinct()
                .collect(Collectors.toList());

        if (attendedDates.isEmpty()) {
            return 0;
        }

        int maxStreak = 1;
        int currentStreak = 1;

        for (int i = 1; i < attendedDates.size(); i++) {
            if (ChronoUnit.DAYS.between(attendedDates.get(i - 1), attendedDates.get(i)) == 1) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }

        return maxStreak;
    }

    private PersonalRecordResponse mapToRecordResponse(PersonalRecord record) {
        // Определяем, является ли этот рекорд лучшим
        List<PersonalRecord> allRecords = personalRecordRepository
                .findByAthleteIdAndExerciseName(record.getAthleteId(), record.getExerciseName());

        boolean isCurrentRecord = allRecords.stream()
                .filter(r -> r.getRecordType() == record.getRecordType())
                .max(Comparator.comparing(PersonalRecord::getRecordValue))
                .map(r -> r.getId().equals(record.getId()))
                .orElse(false);

        return PersonalRecordResponse.builder()
                .id(record.getId())
                .exerciseName(record.getExerciseName())
                .recordType(record.getRecordType().name())
                .recordValue(record.getRecordValue())
                .unit(record.getUnit())
                .achievedDate(record.getAchievedDate())
                .notes(record.getNotes())
                .isCurrentRecord(isCurrentRecord)
                .build();
    }
}