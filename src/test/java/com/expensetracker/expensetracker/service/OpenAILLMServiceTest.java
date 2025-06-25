package com.expensetracker.expensetracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAILLMServiceTest {
    private OpenAILLMService service;

    @BeforeEach
    void setUp() {
        service = new OpenAILLMService();
    }

    @Test
    @DisplayName("Prompt for spending insights contains correct summary data")
    void testBuildSpendingInsightsPrompt() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("totalSpent", 1200.0);
        userData.put("totalIncome", 2000.0);
        userData.put("netAmount", 800.0);
        Map<String, Double> categoryBreakdown = new HashMap<>();
        categoryBreakdown.put("Food", 400.0);
        categoryBreakdown.put("Transport", 200.0);
        userData.put("categoryBreakdown", categoryBreakdown);
        String prompt = service.buildSpendingInsightsPrompt(userData);
        assertThat(prompt).contains("totalSpent=1200.0");
        assertThat(prompt).contains("Food");
        assertThat(prompt).contains("Transport");
    }

    @Test
    @DisplayName("Prompt for category analysis contains correct breakdown")
    void testBuildCategoryAnalysisPrompt() {
        Map<String, Object> categoryBreakdown = new HashMap<>();
        categoryBreakdown.put("Food", 400.0);
        categoryBreakdown.put("Transport", 200.0);
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("age", 30);
        userProfile.put("location", "Delhi");
        String prompt = service.buildCategoryAnalysisPrompt(categoryBreakdown, userProfile);
        assertThat(prompt).contains("Food");
        assertThat(prompt).contains("Delhi");
    }

    // Add more tests for other prompt builders as needed
} 