package com.sport.club.controller;

import com.sport.club.model.dto.response.AchievementResponse;
import com.sport.club.service.AchievementService;
import com.sport.club.service.AthleteService;
import com.sport.club.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final AthleteService athleteService;
    private final TrainingService trainingService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyAchievements(Authentication authentication) {
        try {
            String email = authentication.getName();
            UUID userId = trainingService.getUserIdByEmail(email);

            try {
                UUID athleteId = athleteService.getAthleteIdByUserId(userId);
                List<AchievementResponse> achievements = achievementService.getAthleteAchievements(athleteId);
                return ResponseEntity.ok(achievements);
            } catch (RuntimeException e) {
                return ResponseEntity.status(404).body(new ErrorResponse(404, "Профиль спортсмена не найден"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
        }
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<?> getAthleteAchievements(@PathVariable UUID athleteId) {
        try {
            List<AchievementResponse> achievements = achievementService.getAthleteAchievements(athleteId);
            return ResponseEntity.ok(achievements);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new ErrorResponse(400, e.getMessage()));
        }
    }

    private static class ErrorResponse {
        private final int status;
        private final String message;
        private final String timestamp;

        public ErrorResponse(int status, String message) {
            this.status = status;
            this.message = message;
            this.timestamp = java.time.LocalDateTime.now().toString();
        }

        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public String getTimestamp() { return timestamp; }
    }
}