package com.sport.club.service;

import com.sport.club.model.dto.request.CreateTrainingPlanRequest;
import com.sport.club.model.dto.response.TrainingPlanItemResponse;
import com.sport.club.model.dto.response.TrainingPlanResponse;
import com.sport.club.model.entity.TrainingPlan;
import com.sport.club.model.entity.TrainingPlanItem;
import com.sport.club.model.entity.User;
import com.sport.club.repository.TrainingPlanItemRepository;
import com.sport.club.repository.TrainingPlanRepository;
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
public class TrainingPlanService {

    private final TrainingPlanRepository planRepository;
    private final TrainingPlanItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    public TrainingPlanResponse createPlan(CreateTrainingPlanRequest request, UUID coachId) {
        TrainingPlan plan = new TrainingPlan();
        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setAthleteId(request.getAthleteId());
        plan.setCoachId(coachId);
        plan.setSportType(request.getSportType());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setIsTemplate(request.isTemplate());
        plan.setStatus(TrainingPlan.PlanStatus.DRAFT);

        if (request.getDifficultyLevel() != null) {
            plan.setDifficultyLevel(TrainingPlan.DifficultyLevel.valueOf(request.getDifficultyLevel()));
        }

        TrainingPlan savedPlan = planRepository.save(plan);


        if (request.getItems() != null) {
            for (CreateTrainingPlanRequest.PlanItemRequest itemRequest : request.getItems()) {
                TrainingPlanItem item = new TrainingPlanItem();
                item.setPlanId(savedPlan.getId());
                item.setExerciseName(itemRequest.getExerciseName());
                item.setDescription(itemRequest.getDescription());
                item.setSetsCount(itemRequest.getSetsCount());
                item.setRepsCount(itemRequest.getRepsCount());
                item.setWeight(itemRequest.getWeight());
                item.setDurationMinutes(itemRequest.getDurationMinutes());
                item.setDistanceMeters(itemRequest.getDistanceMeters());
                item.setRestSeconds(itemRequest.getRestSeconds());
                item.setDayNumber(itemRequest.getDayNumber());
                item.setWeekNumber(itemRequest.getWeekNumber());
                item.setScheduledDate(itemRequest.getScheduledDate());

                itemRepository.save(item);
            }
        }

        return getPlanById(savedPlan.getId());
    }

    @Transactional(readOnly = true)
    public List<TrainingPlanResponse> getPlansByAthlete(UUID athleteId) {
        return planRepository.findByAthleteIdOrderByCreatedAtDesc(athleteId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrainingPlanResponse getPlanById(UUID planId) {
        TrainingPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("План тренировок не найден"));
        return mapToResponse(plan);
    }

    @Transactional
    public void updatePlanStatus(UUID planId, String status) {
        TrainingPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("План тренировок не найден"));
        plan.setStatus(TrainingPlan.PlanStatus.valueOf(status));
        planRepository.save(plan);
    }

    @Transactional
    public TrainingPlanItemResponse completePlanItem(UUID itemId, Double actualValue, String notes) {
        TrainingPlanItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Элемент плана не найден"));

        item.setCompleted(true);
        item.setCompletedDate(LocalDateTime.now());
        item.setActualValue(actualValue);
        item.setNotes(notes);

        itemRepository.save(item);


        long totalItems = itemRepository.countByPlanIdAndCompleted(item.getPlanId(), true);
        long allItems = itemRepository.countByPlanIdAndCompleted(item.getPlanId(), false) + totalItems;

        if (totalItems == allItems) {
            TrainingPlan plan = planRepository.findById(item.getPlanId())
                    .orElseThrow(() -> new RuntimeException("План не найден"));
            plan.setStatus(TrainingPlan.PlanStatus.COMPLETED);
            planRepository.save(plan);
        }

        return mapToItemResponse(item);
    }

    private TrainingPlanResponse mapToResponse(TrainingPlan plan) {
        List<TrainingPlanItem> items = itemRepository.findByPlanIdOrderByDayNumberAsc(plan.getId());
        User coach = userRepository.findById(plan.getCoachId()).orElse(null);

        long completedItems = items.stream().filter(TrainingPlanItem::getCompleted).count();
        int totalItems = items.size();
        double progress = totalItems > 0 ? (double) completedItems / totalItems * 100 : 0;

        return TrainingPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .athleteId(plan.getAthleteId())
                .coachId(plan.getCoachId())
                .coachName(coach != null ? coach.getFullName() : null)
                .sportType(plan.getSportType())
                .difficultyLevel(plan.getDifficultyLevel() != null ? plan.getDifficultyLevel().name() : null)
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .status(plan.getStatus().name())
                .isTemplate(plan.getIsTemplate())
                .totalItems(totalItems)
                .completedItems((int) completedItems)
                .progressPercentage(progress)
                .items(items.stream().map(this::mapToItemResponse).collect(Collectors.toList()))
                .createdAt(plan.getCreatedAt())
                .build();
    }

    private TrainingPlanItemResponse mapToItemResponse(TrainingPlanItem item) {
        return TrainingPlanItemResponse.builder()
                .id(item.getId())
                .exerciseName(item.getExerciseName())
                .description(item.getDescription())
                .setsCount(item.getSetsCount())
                .repsCount(item.getRepsCount())
                .weight(item.getWeight())
                .durationMinutes(item.getDurationMinutes())
                .distanceMeters(item.getDistanceMeters())
                .restSeconds(item.getRestSeconds())
                .dayNumber(item.getDayNumber())
                .weekNumber(item.getWeekNumber())
                .scheduledDate(item.getScheduledDate())
                .completed(item.getCompleted())
                .completedDate(item.getCompletedDate())
                .actualValue(item.getActualValue())
                .notes(item.getNotes())
                .build();
    }
}