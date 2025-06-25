package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.GoalRequest;
import com.expensetracker.expensetracker.dto.GoalResponse;
import com.expensetracker.expensetracker.dto.GoalProgressRequest;
import com.expensetracker.expensetracker.entity.*;
import com.expensetracker.expensetracker.repository.GoalRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoalResponse createGoal(Long userId, GoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Goal goal = Goal.builder()
                .user(user)
                .name(request.getName())
                .description(request.getDescription())
                .targetAmount(request.getTargetAmount())
                .currentAmount(BigDecimal.ZERO)
                .goalType(request.getGoalType())
                .targetDate(request.getTargetDate())
                .startDate(request.getStartDate())
                .status(GoalStatus.ACTIVE)
                .build();
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoal(Long goalId, GoalRequest request) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));
        if (goal.getStatus() == GoalStatus.DELETED) {
            throw new IllegalStateException("Cannot update a deleted goal.");
        }
        goal.setName(request.getName());
        goal.setDescription(request.getDescription());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setGoalType(request.getGoalType());
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(request.getStartDate());
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional
    public GoalResponse updateGoalProgress(Long goalId, GoalProgressRequest request) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));
        if (goal.getStatus() == GoalStatus.DELETED) {
            throw new IllegalStateException("Cannot update progress on a deleted goal.");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        BigDecimal newAmount = goal.getCurrentAmount() == null ? request.getAmount() : goal.getCurrentAmount().add(request.getAmount());
        goal.setCurrentAmount(newAmount);
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
        }
        goal = goalRepository.save(goal);
        return toResponse(goal);
    }

    @Transactional(readOnly = true)
    public GoalResponse getGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));
        return toResponse(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalResponse> listUserGoals(Long userId) {
        List<Goal> goals = goalRepository.findByUserId(userId)
            .stream()
            .filter(goal -> goal.getStatus() != GoalStatus.DELETED)
            .collect(Collectors.toList());
        return goals.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));
        goal.setStatus(GoalStatus.DELETED);
        goalRepository.save(goal);
    }

    private GoalResponse toResponse(Goal goal) {
        GoalResponse response = new GoalResponse();
        response.setId(goal.getId());
        response.setUserId(goal.getUser().getId());
        response.setName(goal.getName());
        response.setDescription(goal.getDescription());
        response.setTargetAmount(goal.getTargetAmount());
        response.setCurrentAmount(goal.getCurrentAmount());
        response.setGoalType(goal.getGoalType());
        response.setTargetDate(goal.getTargetDate());
        response.setStartDate(goal.getStartDate());
        response.setStatus(goal.getStatus());
        response.setProgressPercentage(goal.getProgressPercentage());
        response.setCreatedAt(goal.getCreatedAt());
        response.setUpdatedAt(goal.getUpdatedAt());
        return response;
    }
}
