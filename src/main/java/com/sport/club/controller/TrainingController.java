// backend/src/main/java/com/sport/club/controller/TrainingController.java
package com.sport.club.controller;

import com.sport.club.model.dto.request.CreateTrainingRequest;
import com.sport.club.model.dto.response.TrainingResponse;
import com.sport.club.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

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
        UUID athleteId = getUserId(authentication);
        trainingService.registerForTraining(trainingId, athleteId);
        return ResponseEntity.ok("Успешно зарегистрированы на тренировку");
    }

    @DeleteMapping("/{trainingId}/cancel")
    public ResponseEntity<String> cancelRegistration(
            @PathVariable UUID trainingId,
            Authentication authentication) {
        UUID athleteId = getUserId(authentication);
        trainingService.cancelRegistration(trainingId, athleteId);
        return ResponseEntity.ok("Регистрация отменена");
    }

    private UUID getUserId(Authentication authentication) {
        String email = authentication.getName();
        return trainingService.getUserIdByEmail(email);
    }
}