package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.LLMAnalyticsResponse;
import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.AnthropicLLMService;
import com.expensetracker.expensetracker.service.LLMService;
import com.expensetracker.expensetracker.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "ai.llm.provider=anthropic")
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class LLMAnalyticsControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AnthropicLLMService anthropicLLMService;

    @Test
    @DisplayName("GET /api/llm-analytics/spending-insights/{userId} returns correct analytics response")
    void testSpendingInsightsEndpointNormal() throws Exception {
        Long userId = 1L;
        // Mock transactions
        Category food = new Category();
        food.setName("Food");
        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100.00"));
        t1.setCategory(food);
        t1.setTransactionDate(LocalDate.of(2024, 1, 5));
        t1.setTransactionType(Transaction.TransactionType.EXPENSE);
        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200.00"));
        t2.setCategory(food);
        t2.setTransactionDate(LocalDate.of(2024, 1, 10));
        t2.setTransactionType(Transaction.TransactionType.EXPENSE);
        when(transactionRepository.findByUserIdOrderByTransactionDateDesc(eq(userId))).thenReturn(Arrays.asList(t1, t2));
        // Mock LLM response
        when(anthropicLLMService.generateSpendingInsights(any())).thenReturn("You spent a lot on Food.");
        mockMvc.perform(get("/api/llm-analytics/spending-insights/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.llmText").value("You spent a lot on Food."))
                .andExpect(jsonPath("$.visualData.categoryBreakdown.Food").value(300.0))
                .andExpect(jsonPath("$.visualData.totalSpent").value(300.0))
                .andExpect(jsonPath("$.visualData.transactionCount").value(2));
    }

    @Test
    @DisplayName("GET /api/llm-analytics/spending-insights/{userId} returns empty analytics for no transactions")
    void testSpendingInsightsEndpointNoTransactions() throws Exception {
        Long userId = 2L;
        when(transactionRepository.findByUserIdOrderByTransactionDateDesc(eq(userId))).thenReturn(Collections.emptyList());
        when(anthropicLLMService.generateSpendingInsights(any())).thenReturn("No spending data available.");
        mockMvc.perform(get("/api/llm-analytics/spending-insights/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.llmText").value("No spending data available."))
                .andExpect(jsonPath("$.visualData.categoryBreakdown").isEmpty())
                .andExpect(jsonPath("$.visualData.totalSpent").value(0.0))
                .andExpect(jsonPath("$.visualData.transactionCount").value(0));
    }
} 