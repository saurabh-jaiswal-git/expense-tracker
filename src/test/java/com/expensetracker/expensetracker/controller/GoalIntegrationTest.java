package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.Goal;
import com.expensetracker.expensetracker.entity.GoalStatus;
import com.expensetracker.expensetracker.entity.GoalType;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.GoalRepository;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.expensetracker.expensetracker.config.TestSecurityConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class GoalIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        goalRepository.deleteAll();
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        testUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .password("password123")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void testCreateGoal() throws Exception {
        var request = new java.util.HashMap<String, Object>();
        request.put("name", "Vacation");
        request.put("description", "Trip to Goa");
        request.put("targetAmount", "50000");
        request.put("goalType", "SAVINGS");
        request.put("startDate", LocalDate.now().toString());
        request.put("targetDate", LocalDate.now().plusMonths(6).toString());

        mockMvc.perform(post("/api/goals")
                .param("userId", testUser.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vacation"))
                .andExpect(jsonPath("$.description").value("Trip to Goa"))
                .andExpect(jsonPath("$.targetAmount").value(50000))
                .andExpect(jsonPath("$.goalType").value("SAVINGS"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Verify in DB
        var goals = goalRepository.findByUserId(testUser.getId());
        assertEquals(1, goals.size());
        assertEquals("Vacation", goals.get(0).getName());
        assertEquals(GoalStatus.ACTIVE, goals.get(0).getStatus());
    }

    @Test
    void testListGoals_ExcludesDeleted() throws Exception {
        // Create two goals, one active, one deleted
        Goal activeGoal = Goal.builder()
                .user(testUser)
                .name("Active Goal")
                .targetAmount(new BigDecimal("1000"))
                .goalType(GoalType.SAVINGS)
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .status(GoalStatus.ACTIVE)
                .build();
        Goal deletedGoal = Goal.builder()
                .user(testUser)
                .name("Deleted Goal")
                .targetAmount(new BigDecimal("2000"))
                .goalType(GoalType.SAVINGS)
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(2))
                .status(GoalStatus.DELETED)
                .build();
        goalRepository.save(activeGoal);
        goalRepository.save(deletedGoal);

        mockMvc.perform(get("/api/goals/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Active Goal"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[1]").doesNotExist());
    }

    @Test
    void testUpdateGoal() throws Exception {
        Goal goal = Goal.builder()
                .user(testUser)
                .name("Old Name")
                .targetAmount(new BigDecimal("1000"))
                .goalType(GoalType.SAVINGS)
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .status(GoalStatus.ACTIVE)
                .build();
        goal = goalRepository.save(goal);

        var updateRequest = new java.util.HashMap<String, Object>();
        updateRequest.put("name", "New Name");
        updateRequest.put("description", "Updated Desc");
        updateRequest.put("targetAmount", "2000");
        updateRequest.put("goalType", "SAVINGS");
        updateRequest.put("startDate", LocalDate.now().toString());
        updateRequest.put("targetDate", LocalDate.now().plusMonths(2).toString());

        mockMvc.perform(put("/api/goals/" + goal.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.description").value("Updated Desc"))
                .andExpect(jsonPath("$.targetAmount").value(2000));
    }

    @Test
    void testUpdateGoalProgress() throws Exception {
        Goal goal = Goal.builder()
                .user(testUser)
                .name("Progress Goal")
                .targetAmount(new BigDecimal("1000"))
                .goalType(GoalType.SAVINGS)
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .status(GoalStatus.ACTIVE)
                .currentAmount(BigDecimal.ZERO)
                .build();
        goal = goalRepository.save(goal);

        var progressRequest = new java.util.HashMap<String, Object>();
        progressRequest.put("amount", "500");

        mockMvc.perform(put("/api/goals/" + goal.getId() + "/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(progressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentAmount").value(500));
    }

    @Test
    void testDeleteGoal_SoftDelete() throws Exception {
        Goal goal = Goal.builder()
                .user(testUser)
                .name("Delete Me")
                .targetAmount(new BigDecimal("1000"))
                .goalType(GoalType.SAVINGS)
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .status(GoalStatus.ACTIVE)
                .build();
        goal = goalRepository.save(goal);

        mockMvc.perform(delete("/api/goals/" + goal.getId()))
                .andExpect(status().isNoContent());

        Goal deletedGoal = goalRepository.findById(goal.getId()).orElseThrow();
        assertEquals(GoalStatus.DELETED, deletedGoal.getStatus());
    }

    @Test
    void testUpdateGoalProgress_ThrowsOnDeletedGoal() throws Exception {
        Goal goal = Goal.builder()
                .user(testUser)
                .name("Deleted Goal")
                .targetAmount(new BigDecimal("1000"))
                .goalType(GoalType.SAVINGS)
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .status(GoalStatus.DELETED)
                .build();
        goal = goalRepository.save(goal);

        var progressRequest = new java.util.HashMap<String, Object>();
        progressRequest.put("amount", "100");

        mockMvc.perform(put("/api/goals/" + goal.getId() + "/progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(progressRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains("Cannot update progress on a deleted goal.")));
    }

    // Integration tests will be added here
} 