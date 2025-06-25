package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.GoalRequest;
import com.expensetracker.expensetracker.dto.GoalResponse;
import com.expensetracker.expensetracker.dto.GoalProgressRequest;
import com.expensetracker.expensetracker.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
public class GoalController {
    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(
            @RequestParam Long userId,
            @Valid @RequestBody GoalRequest request) {
        log.info("Creating goal for user {}", userId);
        GoalResponse response = goalService.createGoal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<GoalResponse> getGoal(@PathVariable Long goalId) {
        log.info("Getting goal with id: {}", goalId);
        GoalResponse response = goalService.getGoal(goalId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoal(
            @PathVariable Long goalId,
            @Valid @RequestBody GoalRequest request) {
        log.info("Updating goal with id: {}", goalId);
        GoalResponse response = goalService.updateGoal(goalId, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{goalId}/progress")
    public ResponseEntity<GoalResponse> updateGoalProgress(
            @PathVariable Long goalId,
            @Valid @RequestBody GoalProgressRequest request) {
        log.info("Updating progress for goal {} by amount {}", goalId, request.getAmount());
        GoalResponse response = goalService.updateGoalProgress(goalId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalResponse>> listUserGoals(@PathVariable Long userId) {
        log.info("Listing goals for user: {}", userId);
        List<GoalResponse> responses = goalService.listUserGoals(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long goalId) {
        log.info("Marking goal as deleted with id: {}", goalId);
        goalService.deleteGoal(goalId); // Mark as deleted (soft delete)
        return ResponseEntity.noContent().build();
    }
}
