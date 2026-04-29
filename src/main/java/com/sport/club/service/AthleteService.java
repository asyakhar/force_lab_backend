package com.sport.club.service;

import com.sport.club.model.dto.request.RegisterRequest;
import com.sport.club.model.dto.response.AthleteProfileResponse;
import com.sport.club.model.entity.Athlete;
import com.sport.club.model.entity.User;
import com.sport.club.repository.AthleteRepository;
import com.sport.club.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createAthleteProfile(UUID userId, RegisterRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Athlete athlete = new Athlete();
        athlete.setUser(user);
        athlete.setBirthDate(request.getBirthDate());
        athlete.setSportType(request.getSportType());
        athlete.setRank(request.getRank());
        athlete.setHeightCm(request.getHeightCm());
        athlete.setWeightKg(request.getWeightKg());
        athlete.setCoachId(request.getCoachId());
        athlete.setActive(true);

        athleteRepository.save(athlete);
    }

    public UUID getAthleteIdByUserId(UUID userId) {
        Athlete athlete = athleteRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Профиль спортсмена не найден"));
        return athlete.getId();
    }

    public AthleteProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (user.getRole() == User.Role.COACH) {
            throw new RuntimeException("Профиль спортсмена не найден. Вы зарегистрированы как тренер.");
        }

        Athlete athlete = athleteRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Профиль спортсмена не найден. Пожалуйста, завершите регистрацию."));

        User coach = null;
        if (athlete.getCoachId() != null) {
            coach = userRepository.findById(athlete.getCoachId()).orElse(null);
        }

        return AthleteProfileResponse.builder()
                .id(athlete.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .birthDate(athlete.getBirthDate())
                .sportType(athlete.getSportType())
                .rank(athlete.getRank())
                .heightCm(athlete.getHeightCm())
                .weightKg(athlete.getWeightKg())
                .coachId(athlete.getCoachId())
                .coachName(coach != null ? coach.getFullName() : null)
                .medicalGroup(athlete.getMedicalGroup())
                .status(athlete.getActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }

    public AthleteProfileResponse getProfileById(UUID athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Профиль спортсмена не найден"));

        User user = athlete.getUser();
        User coach = null;
        if (athlete.getCoachId() != null) {
            coach = userRepository.findById(athlete.getCoachId()).orElse(null);
        }

        return AthleteProfileResponse.builder()
                .id(athlete.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .birthDate(athlete.getBirthDate())
                .sportType(athlete.getSportType())
                .rank(athlete.getRank())
                .heightCm(athlete.getHeightCm())
                .weightKg(athlete.getWeightKg())
                .coachId(athlete.getCoachId())
                .coachName(coach != null ? coach.getFullName() : null)
                .medicalGroup(athlete.getMedicalGroup())
                .status(athlete.getActive() ? "ACTIVE" : "INACTIVE")
                .build();
    }
}