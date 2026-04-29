package com.sport.club.controller;

import com.sport.club.model.dto.request.CreateTrainingPlanRequest;
import com.sport.club.model.dto.response.TrainingPlanItemResponse;
import com.sport.club.model.dto.response.TrainingPlanResponse;
import com.sport.club.service.TrainingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/training-plans")
@RequiredArgsConstructor
public class TrainingPlanController {

    private final TrainingPlanService trainingPlanService;

    @PostMapping
    public ResponseEntity<TrainingPlanResponse> createPlan(
            @RequestBody CreateTrainingPlanRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        UUID coachId = UUID.randomUUID(); // Упрощенно
        return ResponseEntity.ok(trainingPlanService.createPlan(request, coachId));
    }

    @GetMapping("/athlete/{athleteId}")
    public ResponseEntity<List<TrainingPlanResponse>> getAthletePlans(@PathVariable UUID athleteId) {
        return ResponseEntity.ok(trainingPlanService.getPlansByAthlete(athleteId));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<TrainingPlanResponse> getPlanById(@PathVariable UUID planId) {
        return ResponseEntity.ok(trainingPlanService.getPlanById(planId));
    }

    @PutMapping("/{planId}/status")
    public ResponseEntity<Void> updatePlanStatus(
            @PathVariable UUID planId,
            @RequestBody Map<String, String> request) {
        trainingPlanService.updatePlanStatus(planId, request.get("status"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/items/{itemId}/complete")
    public ResponseEntity<TrainingPlanItemResponse> completePlanItem(
            @PathVariable UUID itemId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(trainingPlanService.completePlanItem(
                itemId,
                request.get("actualValue") != null ? ((Number) request.get("actualValue")).doubleValue() : null,
                (String) request.get("notes")
        ));
    }
}