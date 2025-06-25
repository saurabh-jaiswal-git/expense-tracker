package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.service.DataStrategyService;
import com.expensetracker.expensetracker.service.DataStrategyService.AnalysisResult;
import com.expensetracker.expensetracker.service.DataStrategyService.DataStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.expensetracker.expensetracker.config.TestSecurityConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class SmartAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataStrategyService dataStrategyService;

    private final Long userId = 1L;
    private final LocalDate startDate = LocalDate.of(2024, 1, 1);
    private final LocalDate endDate = LocalDate.of(2024, 12, 31);

    @Test
    @DisplayName("GET /api/smart-analytics/spending-analysis/{userId} returns correct structure and data")
    void testSpendingAnalysisEndpoint() throws Exception {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("strategy", "RAW_DATA");
        metadata.put("transactionCount", 42L);
        metadata.put("performanceMetrics", Map.of("strategySelectionTime", "~0.6ms"));
        AnalysisResult result = new AnalysisResult("insight", DataStrategy.RAW_DATA, 42L, metadata);
        when(dataStrategyService.analyzeWithOptimalStrategy(eq(userId), any(), any())).thenReturn(result);

        mockMvc.perform(get("/api/smart-analytics/spending-analysis/{userId}", userId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.insights").value("insight"))
                .andExpect(jsonPath("$.strategy").value("RAW_DATA"))
                .andExpect(jsonPath("$.transactionCount").value(42))
                .andExpect(jsonPath("$.metadata.strategy").value("RAW_DATA"));
    }

    @Test
    @DisplayName("GET /api/smart-analytics/strategy-recommendations/{userId} returns recommendations")
    void testStrategyRecommendationsEndpoint() throws Exception {
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("recommendedStrategy", "INTELLIGENT_SUMMARY");
        recommendations.put("transactionCount", 500L);
        recommendations.put("reasoning", "Medium dataset");
        recommendations.put("estimatedPerformance", Map.of("estimatedTokens", 200));
        when(dataStrategyService.getStrategyRecommendations(eq(userId), any(), any())).thenReturn(recommendations);

        mockMvc.perform(get("/api/smart-analytics/strategy-recommendations/{userId}", userId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.recommendedStrategy").value("INTELLIGENT_SUMMARY"))
                .andExpect(jsonPath("$.transactionCount").value(500))
                .andExpect(jsonPath("$.reasoning").value("Medium dataset"));
    }

    @Test
    @DisplayName("GET /api/smart-analytics/strategy-comparison/{userId} returns comparison data")
    void testStrategyComparisonEndpoint() throws Exception {
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("recommendedStrategy", "CHUNKED_PROCESSING");
        recommendations.put("transactionCount", 2000L);
        recommendations.put("reasoning", "Large dataset");
        recommendations.put("estimatedPerformance", Map.of("estimatedTokens", 500));
        when(dataStrategyService.getStrategyRecommendations(eq(userId), any(), any())).thenReturn(recommendations);

        mockMvc.perform(get("/api/smart-analytics/strategy-comparison/{userId}", userId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.transactionCount").value(2000))
                .andExpect(jsonPath("$.recommendedStrategy").value("CHUNKED_PROCESSING"))
                .andExpect(jsonPath("$.strategies.RAW_DATA").exists())
                .andExpect(jsonPath("$.strategies.INTELLIGENT_SUMMARY").exists())
                .andExpect(jsonPath("$.strategies.CHUNKED_PROCESSING").exists());
    }

    @Test
    @DisplayName("GET /api/smart-analytics/performance-metrics/{userId} returns performance metrics")
    void testPerformanceMetricsEndpoint() throws Exception {
        Map<String, Object> recommendations = new HashMap<>();
        recommendations.put("recommendedStrategy", "RAW_DATA");
        recommendations.put("transactionCount", 42L);
        recommendations.put("estimatedPerformance", Map.of("estimatedTokens", 1000));
        when(dataStrategyService.getStrategyRecommendations(eq(userId), any(), any())).thenReturn(recommendations);

        mockMvc.perform(get("/api/smart-analytics/performance-metrics/{userId}", userId)
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.transactionCount").value(42))
                .andExpect(jsonPath("$.recommendedStrategy").value("RAW_DATA"))
                .andExpect(jsonPath("$.estimatedPerformance.estimatedTokens").value(1000))
                .andExpect(jsonPath("$.performanceInsights.strategySelectionTime").exists());
    }
} 