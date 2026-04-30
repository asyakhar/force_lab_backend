package com.sport.club.service;

import com.sport.club.controller.SseController;
import com.sport.club.model.dto.request.CreateTrainingRequest;
import com.sport.club.model.dto.response.TrainingResponse;
import com.sport.club.model.entity.Athlete;
import com.sport.club.model.entity.Training;
import com.sport.club.model.entity.TrainingAttendance;
import com.sport.club.model.entity.User;
import com.sport.club.repository.AthleteRepository;
import com.sport.club.repository.TrainingAttendanceRepository;
import com.sport.club.repository.TrainingRepository;
import com.sport.club.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingAttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final AthleteRepository athleteRepository;
    private final SseController sseController;  // ← ДОБАВИТЬ

    @Transactional(readOnly = true)
    public List<TrainingResponse> getUpcomingTrainings() {
        List<Training> trainings = trainingRepository.findUpcomingTrainings(LocalDateTime.now());
        return trainings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainingResponse createTraining(CreateTrainingRequest request, UUID coachId) {
        Training training = new Training();
        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setTrainingDate(request.getTrainingDate());
        training.setDurationMinutes(request.getDurationMinutes());
        training.setLocation(request.getLocation());
        training.setSportType(request.getSportType());
        training.setCoachId(coachId);
        training.setMaxParticipants(request.getMaxParticipants());

        Training saved = trainingRepository.save(training);

        // ← ДОБАВИТЬ: Уведомляем всех о новой тренировке
        sseController.sendEventToAll("training-updated",
                Map.of("message", "Создана новая тренировка: " + saved.getTitle()));

        return mapToResponse(saved);
    }

    @Transactional
    public void registerForTraining(UUID trainingId, UUID userId) {
        Athlete athlete = athleteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Профиль спортсмена не найден"));

        UUID athleteId = athlete.getId();

        Optional<TrainingAttendance> existingAttendance = attendanceRepository
                .findByTrainingIdAndAthleteId(trainingId, athleteId);

        if (existingAttendance.isPresent()) {
            TrainingAttendance attendance = existingAttendance.get();

            if ("CANCELLED".equals(attendance.getStatus())) {
                attendance.setStatus("REGISTERED");
                attendance.setMarkedAt(LocalDateTime.now());
                attendanceRepository.save(attendance);

                // SSE уведомление
                sseController.sendEventToAll("training-updated",
                        Map.of("message", "Участник перезаписался"));
                return;
            } else {
                throw new RuntimeException("Вы уже зарегистрированы на эту тренировку");
            }
        }

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Тренировка не найдена"));

        if (training.getMaxParticipants() != null) {
            long currentParticipants = attendanceRepository.findByTrainingId(training.getId())
                    .stream()
                    .filter(a -> !"CANCELLED".equals(a.getStatus()))
                    .count();
            if (currentParticipants >= training.getMaxParticipants()) {
                throw new RuntimeException("Нет свободных мест на тренировку");
            }
        }

        if (training.getCoachId() != null) {
            athlete.setCoachId(training.getCoachId());
            athleteRepository.save(athlete);
            System.out.println("✅ Спортсмен " + athlete.getUser().getFullName() + " привязан к тренеру");
        }

        TrainingAttendance attendance = new TrainingAttendance();
        attendance.setTrainingId(trainingId);
        attendance.setAthleteId(athleteId);
        attendance.setStatus("REGISTERED");
        attendance.setMarkedAt(LocalDateTime.now());

        attendanceRepository.save(attendance);

        // ← ДОБАВИТЬ: Уведомления
        sseController.sendEventToAll("training-updated",
                Map.of("message", "Обновление участников"));
        sseController.sendEvent(training.getCoachId().toString(), "participant-added",
                Map.of("trainingId", trainingId.toString(),
                        "athleteName", athlete.getUser().getFullName()));
    }

    @Transactional
    public void cancelRegistration(UUID trainingId, UUID userId) {
        Athlete athlete = athleteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Профиль спортсмена не найден"));

        UUID athleteId = athlete.getId();

        TrainingAttendance attendance = attendanceRepository
                .findByTrainingIdAndAthleteId(trainingId, athleteId)
                .orElseThrow(() -> new RuntimeException("Вы не зарегистрированы на эту тренировку"));

        attendance.setStatus("CANCELLED");
        attendanceRepository.save(attendance);

        // ← ДОБАВИТЬ: Уведомления
        sseController.sendEventToAll("training-updated",
                Map.of("message", "Участник отменил запись"));
    }

    @Transactional
    public void markAttendance(UUID trainingId, UUID athleteId, String status) {
        TrainingAttendance attendance = attendanceRepository
                .findByTrainingIdAndAthleteId(trainingId, athleteId)
                .orElseThrow(() -> new RuntimeException("Спортсмен не записан на эту тренировку"));

        attendance.setStatus(status);
        attendanceRepository.save(attendance);

        System.out.println("✅ Посещение отмечено: athleteId=" + athleteId + ", status=" + status);

        // ← ДОБАВИТЬ: Уведомления
        sseController.sendEvent(athleteId.toString(), "attendance-marked",
                Map.of("status", status, "message", "Ваше посещение отмечено как " + status));
        sseController.sendEventToAll("training-updated",
                Map.of("message", "Обновление статуса посещения"));
    }

    public UUID getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getId();
    }

    public List<TrainingResponse> getAthleteTrainings(UUID athleteId) {
        List<TrainingAttendance> attendances = attendanceRepository.findByAthleteId(athleteId);

        return attendances.stream()
                .filter(a -> !"CANCELLED".equals(a.getStatus()))
                .map(attendance -> {
                    Training training = trainingRepository.findById(attendance.getTrainingId())
                            .orElse(null);
                    if (training == null) return null;
                    return mapToResponse(training);
                })
                .filter(Objects::nonNull)
                .sorted((t1, t2) -> t2.getTrainingDate().compareTo(t1.getTrainingDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainingResponse updateTraining(UUID trainingId, CreateTrainingRequest request, UUID coachId) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Тренировка не найдена"));

        if (!training.getCoachId().equals(coachId)) {
            throw new RuntimeException("Вы не можете редактировать эту тренировку");
        }

        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setTrainingDate(request.getTrainingDate());
        training.setDurationMinutes(request.getDurationMinutes());
        training.setLocation(request.getLocation());
        training.setSportType(request.getSportType());
        training.setMaxParticipants(request.getMaxParticipants());

        Training saved = trainingRepository.save(training);

        // ← ДОБАВИТЬ
        sseController.sendEventToAll("training-updated",
                Map.of("message", "Тренировка обновлена: " + saved.getTitle()));

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteTraining(UUID trainingId, UUID coachId) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Тренировка не найдена"));

        if (!training.getCoachId().equals(coachId)) {
            throw new RuntimeException("Вы не можете удалить эту тренировку");
        }

        List<TrainingAttendance> attendances = attendanceRepository.findByTrainingId(trainingId);
        attendanceRepository.deleteAll(attendances);
        trainingRepository.delete(training);

        // ← ДОБАВИТЬ
        sseController.sendEventToAll("training-updated",
                Map.of("message", "Тренировка удалена"));
    }

    public TrainingResponse getTrainingDetails(UUID trainingId) {
        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Тренировка не найдена"));
        return mapToResponse(training);
    }

    public List<Map<String, Object>> getParticipants(UUID trainingId) {
        List<TrainingAttendance> attendances = attendanceRepository.findByTrainingId(trainingId);
        List<Map<String, Object>> participants = new ArrayList<>();

        for (TrainingAttendance attendance : attendances) {
            if ("CANCELLED".equals(attendance.getStatus())) {
                continue;
            }

            Map<String, Object> participant = new LinkedHashMap<>();
            participant.put("id", attendance.getId());
            participant.put("status", attendance.getStatus());
            participant.put("registeredAt", attendance.getMarkedAt());

            try {
                Optional<Athlete> athleteOpt = athleteRepository.findById(attendance.getAthleteId());
                if (athleteOpt.isPresent()) {
                    Athlete athlete = athleteOpt.get();
                    User user = athlete.getUser();
                    participant.put("athleteId", athlete.getId());
                    participant.put("fullName", user.getFullName() != null ? user.getFullName() : "Без имени");
                    participant.put("email", user.getEmail() != null ? user.getEmail() : "Нет email");
                    participant.put("sportType", athlete.getSportType() != null ? athlete.getSportType() : "Не указан");
                    participant.put("rank", athlete.getRank() != null ? athlete.getRank() : "Без разряда");
                    participant.put("phone", user.getPhone() != null ? user.getPhone() : "");
                } else {
                    participant.put("fullName", "Спортсмен #" + attendance.getAthleteId());
                    participant.put("email", "Нет данных");
                    participant.put("sportType", "Не указан");
                    participant.put("rank", "Нет");
                }
            } catch (Exception e) {
                participant.put("fullName", "Ошибка загрузки");
                participant.put("email", "");
                participant.put("sportType", "");
                participant.put("rank", "");
            }

            participants.add(participant);
        }

        return participants;
    }

    private TrainingResponse mapToResponse(Training training) {
        User coach = userRepository.findById(training.getCoachId()).orElse(null);
        long participants = attendanceRepository.findByTrainingId(training.getId())
                .stream()
                .filter(a -> !"CANCELLED".equals(a.getStatus()))
                .count();

        return TrainingResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .trainingDate(training.getTrainingDate())
                .durationMinutes(training.getDurationMinutes())
                .location(training.getLocation())
                .sportType(training.getSportType())
                .maxParticipants(training.getMaxParticipants())
                .currentParticipants((int) participants)
                .coachName(coach != null ? coach.getFullName() : null)
                .coachId(training.getCoachId())
                .build();
    }
}