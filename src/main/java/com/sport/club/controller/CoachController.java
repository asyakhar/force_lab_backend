package com.sport.club.controller;

import com.sport.club.model.dto.response.AthleteProfileResponse;
import com.sport.club.model.dto.response.TrainingPlanResponse;
import com.sport.club.model.entity.Athlete;
import com.sport.club.model.entity.User;
import com.sport.club.repository.AthleteRepository;
import com.sport.club.repository.UserRepository;
import com.sport.club.service.AthleteService;
import com.sport.club.service.TrainingPlanService;
import com.sport.club.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coach")
@RequiredArgsConstructor
public class CoachController {

    private final UserRepository userRepository;
    private final AthleteService athleteService;
    private final TrainingService trainingService;
    private final TrainingPlanService trainingPlanService;
    private final AthleteRepository athleteRepository;

    @GetMapping("/athletes")
    public ResponseEntity<?> getMyAthletes(Authentication authentication) {
        try {
            String email = authentication.getName();
            User coach = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Тренер не найден"));

            System.out.println("Тренер: " + coach.getFullName() + " (ID: " + coach.getId() + ")");


            List<Athlete> myAthletes = athleteRepository.findByCoachId(coach.getId());
            System.out.println("Найдено спортсменов: " + myAthletes.size());

            List<Map<String, Object>> athletesList = new ArrayList<>();

            for (Athlete athlete : myAthletes) {
                User athleteUser = athlete.getUser();

                Map<String, Object> athleteData = new LinkedHashMap<>();
                athleteData.put("id", athlete.getId());
                athleteData.put("fullName", athleteUser.getFullName());
                athleteData.put("email", athleteUser.getEmail());
                athleteData.put("phone", athleteUser.getPhone() != null ? athleteUser.getPhone() : "");
                athleteData.put("sportType", athlete.getSportType() != null ? athlete.getSportType() : "Не указан");
                athleteData.put("rank", athlete.getRank() != null ? athlete.getRank() : "Без разряда");
                athleteData.put("status", athlete.getActive() ? "ACTIVE" : "INACTIVE");

                athletesList.add(athleteData);
                System.out.println(" " + athleteUser.getFullName() + " - " + athlete.getSportType());
            }

            return ResponseEntity.ok(athletesList);
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/athletes/{athleteId}/progress")
    public ResponseEntity<?> getAthleteProgress(@PathVariable UUID athleteId) {
        try {

            Map<String, Object> progress = new LinkedHashMap<>();


            AthleteProfileResponse profile = athleteService.getProfileById(athleteId);
            progress.put("profile", profile);
            progress.put("totalTrainings", 0);
            progress.put("attendanceRate", 0.0);
            progress.put("lastTraining", null);

            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @PostMapping("/athletes/{athleteId}/plans")
    public ResponseEntity<?> assignPlanToAthlete(
            @PathVariable UUID athleteId,
            @RequestBody Map<String, Object> planData,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User coach = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Тренер не найден"));


            Map<String, Object> request = new LinkedHashMap<>();
            request.put("name", planData.get("name"));
            request.put("description", planData.get("description"));
            request.put("athleteId", athleteId.toString());
            request.put("sportType", planData.get("sportType"));
            request.put("difficultyLevel", planData.get("difficultyLevel"));
            request.put("startDate", planData.get("startDate"));
            request.put("endDate", planData.get("endDate"));
            request.put("isTemplate", false);




            return ResponseEntity.ok(Map.of("message", "План назначен"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }


    @GetMapping("/stats")
    public ResponseEntity<?> getCoachStats(Authentication authentication) {
        try {
            String email = authentication.getName();
            User coach = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Тренер не найден"));

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalAthletes", 0);
            stats.put("totalTrainings", 0);
            stats.put("activePlans", 0);
            stats.put("averageAttendance", 0.0);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}