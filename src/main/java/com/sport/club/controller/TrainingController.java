package com.sport.club.controller;

import com.sport.club.model.dto.request.CreateTrainingRequest;
import com.sport.club.model.dto.response.TrainingResponse;
import com.sport.club.model.entity.User;
import com.sport.club.repository.UserRepository;
import com.sport.club.service.AthleteService;
import com.sport.club.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;
    private final UserRepository userRepository;
    private final AthleteService athleteService;

    @GetMapping("/upcoming")
    public ResponseEntity<List<TrainingResponse>> getUpcomingTrainings() {
        return ResponseEntity.ok(trainingService.getUpcomingTrainings());
    }

    @PostMapping
    public ResponseEntity<TrainingResponse> createTraining(
            @RequestBody CreateTrainingRequest request,
            Authentication authentication) {
        UUID coachId = getUserId(authentication);
        return ResponseEntity.ok(trainingService.createTraining(request, coachId));
    }

    @PostMapping("/{trainingId}/register")
    public ResponseEntity<String> registerForTraining(
            @PathVariable UUID trainingId,
            Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Передаем userId, а не athleteId
        trainingService.registerForTraining(trainingId, user.getId());
        return ResponseEntity.ok("Успешно зарегистрированы на тренировку");
    }

    @DeleteMapping("/{trainingId}/cancel")
    public ResponseEntity<String> cancelRegistration(
            @PathVariable UUID trainingId,
            Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        trainingService.cancelRegistration(trainingId, user.getId());
        return ResponseEntity.ok("Регистрация отменена");
    }

    // Обновить тренировку
    @PutMapping("/{trainingId}")
    public ResponseEntity<?> updateTraining(
            @PathVariable UUID trainingId,
            @RequestBody CreateTrainingRequest request,
            Authentication authentication) {
        try {
            UUID coachId = getUserId(authentication);
            TrainingResponse updated = trainingService.updateTraining(trainingId, request, coachId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Удалить тренировку
    @DeleteMapping("/{trainingId}")
    public ResponseEntity<?> deleteTraining(
            @PathVariable UUID trainingId,
            Authentication authentication) {
        try {
            UUID coachId = getUserId(authentication);
            trainingService.deleteTraining(trainingId, coachId);
            return ResponseEntity.ok(Map.of("message", "Тренировка удалена"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Получить детальную информацию о тренировке
    @GetMapping("/{trainingId}/details")
    public ResponseEntity<?> getTrainingDetails(@PathVariable UUID trainingId) {
        try {
            TrainingResponse training = trainingService.getTrainingDetails(trainingId);
            return ResponseEntity.ok(training);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Получить список записавшихся спортсменов
    @GetMapping("/{trainingId}/participants")
    public ResponseEntity<?> getTrainingParticipants(@PathVariable UUID trainingId) {
        try {
            List<Map<String, Object>> participants = trainingService.getParticipants(trainingId);
            return ResponseEntity.ok(participants);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyTrainings(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            // Получаем athleteId
            UUID userId = user.getId();
            UUID athleteId;

            try {
                athleteId = athleteService.getAthleteIdByUserId(userId);
            } catch (Exception e) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<TrainingResponse> trainings = trainingService.getAthleteTrainings(athleteId);
            // System.out.println("🔍 Найдено тренировок: " + trainings.size()); // ЛОГ
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage()); // ЛОГ
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{trainingId}/attendance/{athleteId}")
    public ResponseEntity<?> markAttendance(
            @PathVariable UUID trainingId,
            @PathVariable UUID athleteId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status"); // ATTENDED, ABSENT, LATE
            trainingService.markAttendance(trainingId, athleteId, status);
            return ResponseEntity.ok(Map.of("message", "Посещение отмечено"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    private UUID getUserId(Authentication authentication) {
        String email = authentication.getName();
        return trainingService.getUserIdByEmail(email);
    }

    // Активные (можно отметить — прошли, но не прошло 24 часа)
    @GetMapping("/active-for-marking")
    public ResponseEntity<List<TrainingResponse>> getActiveForMarking() {
        return ResponseEntity.ok(trainingService.getActiveForMarking());
    }

    // Завершенные (прошло больше 24 часов)
    @GetMapping("/completed")
    public ResponseEntity<List<TrainingResponse>> getCompletedTrainings() {
        return ResponseEntity.ok(trainingService.getCompletedTrainings());
    }

    @GetMapping("/my-with-status")
    public ResponseEntity<?> getMyTrainingsWithStatus(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            UUID athleteId;
            try {
                athleteId = athleteService.getAthleteIdByUserId(user.getId());
            } catch (Exception e) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<Map<String, Object>> trainings = trainingService.getAthleteTrainingsWithStatus(athleteId);
            return ResponseEntity.ok(trainings);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage()));
        }
    }
}