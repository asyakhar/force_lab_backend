package com.sport.club.controller;

import com.sport.club.model.dto.response.PersonalRecordResponse;
import com.sport.club.model.dto.response.ProgressStatsResponse;
import com.sport.club.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping("/stats/{athleteId}")
    public ResponseEntity<?> getProgressStats(@PathVariable UUID athleteId) {
        try {
            ProgressStatsResponse stats = progressService.getProgressStats(athleteId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of(
                            "status", 400,
                            "message", "Ошибка получения статистики: " + e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }

    @PostMapping("/records/{athleteId}")
    public ResponseEntity<?> addPersonalRecord(
            @PathVariable UUID athleteId,
            @RequestBody Map<String, Object> request) {
        try {
            PersonalRecordResponse record = progressService.addPersonalRecord(
                    athleteId,
                    (String) request.get("exerciseName"),
                    (String) request.get("recordType"),
                    ((Number) request.get("value")).doubleValue(),
                    (String) request.get("unit"),
                    (String) request.get("notes")
            );
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of(
                            "status", 400,
                            "message", "Ошибка добавления рекорда: " + e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }
}