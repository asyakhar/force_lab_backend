package com.sport.club.controller;

import com.sport.club.model.dto.response.AthleteProfileResponse;
import com.sport.club.service.AthleteService;
import com.sport.club.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteController {

    private final AthleteService athleteService;
    private final TrainingService trainingService;

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            AthleteProfileResponse profile = athleteService.getProfileByEmail(email);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(404, e.getMessage()));
        }
    }

    @GetMapping("/{athleteId}")
    public ResponseEntity<AthleteProfileResponse> getAthleteProfile(@PathVariable UUID athleteId) {
        return ResponseEntity.ok(athleteService.getProfileById(athleteId));
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