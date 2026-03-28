package com.sport.club.service;

import com.sport.club.model.dto.request.CreateTrainingRequest;
import com.sport.club.model.dto.response.TrainingResponse;
import com.sport.club.model.entity.Training;
import com.sport.club.model.entity.TrainingAttendance;
import com.sport.club.model.entity.User;
import com.sport.club.repository.TrainingAttendanceRepository;
import com.sport.club.repository.TrainingRepository;
import com.sport.club.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingAttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TrainingResponse> getUpcomingTrainings() {
        List<Training> trainings = trainingRepository.findUpcomingTrainings(LocalDateTime.now());
        return trainings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainingResponse createTraining(CreateTrainingRequest request, UUID coachId) {
        Training training = new Training();
        training.setTitle(request.getTitle());
        training.setDescription(request.getDescription());
        training.setTrainingDate(request.getTrainingDate());
        training.setDurationMinutes(request.getDurationMinutes());
        training.setLocation(request.getLocation());
        training.setSportType(request.getSportType());
        training.setCoachId(coachId);
        training.setMaxParticipants(request.getMaxParticipants());

        Training saved = trainingRepository.save(training);
        return mapToResponse(saved);
    }

    @Transactional
    public void registerForTraining(UUID trainingId, UUID athleteId) {
        if (attendanceRepository.findByTrainingIdAndAthleteId(trainingId, athleteId).isPresent()) {
            throw new RuntimeException("Вы уже зарегистрированы на эту тренировку");
        }

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new RuntimeException("Тренировка не найдена"));

        if (training.getMaxParticipants() != null) {
            long currentParticipants = attendanceRepository.countByTrainingIdAndStatus(trainingId, "PRESENT");
            if (currentParticipants >= training.getMaxParticipants()) {
                throw new RuntimeException("Нет свободных мест на тренировку");
            }
        }

        TrainingAttendance attendance = new TrainingAttendance();
        attendance.setTrainingId(trainingId);
        attendance.setAthleteId(athleteId);
        attendance.setStatus("PRESENT");
        attendance.setMarkedAt(LocalDateTime.now());

        attendanceRepository.save(attendance);
    }

    @Transactional
    public void cancelRegistration(UUID trainingId, UUID athleteId) {
        TrainingAttendance attendance = attendanceRepository
                .findByTrainingIdAndAthleteId(trainingId, athleteId)
                .orElseThrow(() -> new RuntimeException("Вы не зарегистрированы на эту тренировку"));

        attendanceRepository.delete(attendance);
    }

    public UUID getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getId();
    }

    private TrainingResponse mapToResponse(Training training) {
        User coach = userRepository.findById(training.getCoachId()).orElse(null);
        long participants = attendanceRepository.countByTrainingIdAndStatus(training.getId(), "PRESENT");

        return TrainingResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .trainingDate(training.getTrainingDate())
                .durationMinutes(training.getDurationMinutes())
                .location(training.getLocation())
                .sportType(training.getSportType())
                .maxParticipants(training.getMaxParticipants())
                .currentParticipants((int) participants)
                .coachName(coach != null ? coach.getFullName() : null)
                .coachId(training.getCoachId())
                .build();
    }

}