package com.sport.club.controller;

import com.sport.club.model.dto.response.AthleteProfileResponse;

import com.sport.club.model.entity.User;
import com.sport.club.repository.UserRepository;
import com.sport.club.service.AthleteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final AthleteService athleteService;

    @GetMapping
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (user.getRole() == User.Role.COACH) {
                return ResponseEntity.ok(getCoachProfile(user));
            } else {
                return ResponseEntity.ok(getAthleteProfile(user));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of(
                            "status", 400,
                            "message", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }

    private Map<String, Object> getCoachProfile(User user) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("fullName", user.getFullName());
        profile.put("phone", user.getPhone());
        profile.put("role", "COACH");
        profile.put("createdAt", user.getCreatedAt());

        // Статистика тренера
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalAthletes", 0); // Можно добавить реальный подсчет
        stats.put("totalTrainings", 0);
        stats.put("rating", 0.0);
        profile.put("stats", stats);

        // Специализации (можно добавить в сущность User)
        List<String> specializations = Arrays.asList("ОФП", "Силовая подготовка");
        profile.put("specializations", specializations);

        return profile;
    }

    private Map<String, Object> getAthleteProfile(User user) {
        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("fullName", user.getFullName());
        profile.put("phone", user.getPhone());
        profile.put("role", "ATHLETE");
        profile.put("createdAt", user.getCreatedAt());

        try {
            AthleteProfileResponse athleteProfile = athleteService.getProfileByEmail(user.getEmail());
            profile.put("athleteInfo", athleteProfile);

            // Статистика спортсмена
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("totalTrainings", 0);
            stats.put("totalRecords", 0);
            stats.put("achievements", 0);
            profile.put("stats", stats);
        } catch (Exception e) {
            profile.put("athleteInfo", null);
        }

        return profile;
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates,
                                           Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            if (updates.containsKey("fullName")) {
                user.setFullName((String) updates.get("fullName"));
            }
            if (updates.containsKey("phone")) {
                user.setPhone((String) updates.get("phone"));
            }

            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Профиль обновлен"));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(Map.of(
                            "status", 400,
                            "message", e.getMessage(),
                            "timestamp", java.time.LocalDateTime.now().toString()
                    ));
        }
    }
}