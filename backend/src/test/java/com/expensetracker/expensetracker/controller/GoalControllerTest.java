package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.GoalRequest;
import com.expensetracker.expensetracker.dto.GoalResponse;
import com.expensetracker.expensetracker.dto.GoalProgressRequest;
import com.expensetracker.expensetracker.entity.GoalStatus;
import com.expensetracker.expensetracker.entity.GoalType;
import com.expensetracker.expensetracker.service.GoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GoalControllerTest {
    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateGoal() {
        GoalRequest request = new GoalRequest();
        request.setName("Vacation");
        request.setDescription("Trip to Goa");
        request.setTargetAmount(new BigDecimal("50000"));
        request.setGoalType(GoalType.SAVINGS);
        request.setStartDate(LocalDate.now());
        request.setTargetDate(LocalDate.now().plusMonths(6));

        GoalResponse mockResponse = new GoalResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Vacation");
        mockResponse.setDescription("Trip to Goa");
        mockResponse.setTargetAmount(new BigDecimal("50000"));
        mockResponse.setGoalType(GoalType.SAVINGS);
        mockResponse.setStartDate(request.getStartDate());
        mockResponse.setTargetDate(request.getTargetDate());
        mockResponse.setStatus(GoalStatus.ACTIVE);

        when(goalService.createGoal(eq(1L), any(GoalRequest.class))).thenReturn(mockResponse);

        ResponseEntity<GoalResponse> response = goalController.createGoal(1L, request);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Vacation", response.getBody().getName());
        assertEquals(GoalStatus.ACTIVE, response.getBody().getStatus());
    }

    @Test
    void testListUserGoals_ExcludesDeleted() {
        GoalResponse activeGoal = new GoalResponse();
        activeGoal.setId(1L);
        activeGoal.setName("Active Goal");
        activeGoal.setStatus(GoalStatus.ACTIVE);
        GoalResponse deletedGoal = new GoalResponse();
        deletedGoal.setId(2L);
        deletedGoal.setName("Deleted Goal");
        deletedGoal.setStatus(GoalStatus.DELETED);
        when(goalService.listUserGoals(1L)).thenReturn(List.of(activeGoal)); // Service already filters

        ResponseEntity<List<GoalResponse>> response = goalController.listUserGoals(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Active Goal", response.getBody().get(0).getName());
    }

    @Test
    void testUpdateGoal() {
        GoalRequest updateRequest = new GoalRequest();
        updateRequest.setName("Updated Goal");
        updateRequest.setDescription("Updated Desc");
        updateRequest.setTargetAmount(new BigDecimal("10000"));
        updateRequest.setGoalType(GoalType.SAVINGS);
        updateRequest.setStartDate(LocalDate.now());
        updateRequest.setTargetDate(LocalDate.now().plusMonths(2));

        GoalResponse updatedResponse = new GoalResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Goal");
        updatedResponse.setDescription("Updated Desc");
        updatedResponse.setTargetAmount(new BigDecimal("10000"));
        updatedResponse.setGoalType(GoalType.SAVINGS);
        updatedResponse.setStartDate(updateRequest.getStartDate());
        updatedResponse.setTargetDate(updateRequest.getTargetDate());
        updatedResponse.setStatus(GoalStatus.ACTIVE);

        when(goalService.updateGoal(eq(1L), any(GoalRequest.class))).thenReturn(updatedResponse);

        ResponseEntity<GoalResponse> response = goalController.updateGoal(1L, updateRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated Goal", response.getBody().getName());
    }

    @Test
    void testUpdateGoalProgress() {
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("2000"));

        GoalResponse progressResponse = new GoalResponse();
        progressResponse.setId(1L);
        progressResponse.setName("Vacation");
        progressResponse.setCurrentAmount(new BigDecimal("2000"));
        progressResponse.setStatus(GoalStatus.ACTIVE);

        when(goalService.updateGoalProgress(eq(1L), any(GoalProgressRequest.class))).thenReturn(progressResponse);

        ResponseEntity<GoalResponse> response = goalController.updateGoalProgress(1L, progressRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new BigDecimal("2000"), response.getBody().getCurrentAmount());
    }

    @Test
    void testDeleteGoal_SoftDelete() {
        doNothing().when(goalService).deleteGoal(1L);
        ResponseEntity<Void> response = goalController.deleteGoal(1L);
        assertEquals(204, response.getStatusCodeValue());
        verify(goalService, times(1)).deleteGoal(1L);
    }

    @Test
    void testUpdateGoal_ThrowsOnDeletedGoal() {
        GoalRequest updateRequest = new GoalRequest();
        updateRequest.setName("Should Fail");
        updateRequest.setDescription("Should Fail");
        updateRequest.setTargetAmount(new BigDecimal("100"));
        updateRequest.setGoalType(GoalType.SAVINGS);
        updateRequest.setStartDate(LocalDate.now());
        updateRequest.setTargetDate(LocalDate.now().plusDays(1));

        when(goalService.updateGoal(eq(1L), any(GoalRequest.class)))
                .thenThrow(new IllegalStateException("Cannot update a deleted goal."));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                goalController.updateGoal(1L, updateRequest));
        assertEquals("Cannot update a deleted goal.", ex.getMessage());
    }

    @Test
    void testUpdateGoalProgress_ThrowsOnDeletedGoal() {
        GoalProgressRequest progressRequest = new GoalProgressRequest();
        progressRequest.setAmount(new BigDecimal("100"));
        when(goalService.updateGoalProgress(eq(1L), any(GoalProgressRequest.class)))
                .thenThrow(new IllegalStateException("Cannot update progress on a deleted goal."));

        Exception ex = assertThrows(IllegalStateException.class, () ->
                goalController.updateGoalProgress(1L, progressRequest));
        assertEquals("Cannot update progress on a deleted goal.", ex.getMessage());
    }
} 