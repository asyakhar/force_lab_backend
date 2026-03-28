
package com.sport.club.service;

import com.sport.club.model.dto.request.RegisterRequest;
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
}