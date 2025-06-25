package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.LLMAnalyticsResponse;
import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.LLMService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class LLMAnalyticsControllerTest {
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LLMService llmService;
    @InjectMocks
    private LLMAnalyticsController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Returns correct response for normal spending insights")
    void testSpendingInsightsNormal() {
        Transaction t1 = new Transaction();
        t1.setAmount(new BigDecimal("100.00"));
        t1.setTransactionType(Transaction.TransactionType.EXPENSE);
        Category food = new Category();
        food.setName("Food");
        t1.setCategory(food);
        t1.setTransactionDate(LocalDate.now());

        Transaction t2 = new Transaction();
        t2.setAmount(new BigDecimal("200.00"));
        t2.setTransactionType(Transaction.TransactionType.EXPENSE);
        Category transport = new Category();
        transport.setName("Transport");
        t2.setCategory(transport);
        t2.setTransactionDate(LocalDate.now());

        List<Transaction> txns = Arrays.asList(t1, t2);
        when(transactionRepository.findByUserIdOrderByTransactionDateDesc(anyLong())).thenReturn(txns);
        when(llmService.generateSpendingInsights(anyMap())).thenReturn("You spent 100 on Food and 200 on Transport.");

        ResponseEntity<LLMAnalyticsResponse> resp = controller.getSpendingInsights(1L, null, null);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        LLMAnalyticsResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getLlmText()).contains("Food").contains("Transport");
        assertThat(body.getVisualData()).containsKey("categoryBreakdown");
        Map<String, Double> breakdown = (Map<String, Double>) body.getVisualData().get("categoryBreakdown");
        assertThat(breakdown.get("Food")).isEqualTo(100.0);
        assertThat(breakdown.get("Transport")).isEqualTo(200.0);
    }

    @Test
    @DisplayName("Returns empty breakdown and text for no transactions")
    void testSpendingInsightsNoTransactions() {
        when(transactionRepository.findByUserIdOrderByTransactionDateDesc(anyLong())).thenReturn(Collections.emptyList());
        when(llmService.generateSpendingInsights(anyMap())).thenReturn("No spending data available.");
        ResponseEntity<LLMAnalyticsResponse> resp = controller.getSpendingInsights(1L, null, null);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        LLMAnalyticsResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getLlmText()).contains("No spending data");
        Map<String, Object> visual = body.getVisualData();
        assertThat(visual.get("categoryBreakdown")).isInstanceOf(Map.class);
        assertThat(((Map<?, ?>) visual.get("categoryBreakdown")).isEmpty()).isTrue();
        assertThat(visual.get("totalSpent")).isEqualTo(0.0);
        assertThat(visual.get("transactionCount")).isEqualTo(0);
    }

    // Add more edge case tests as needed
} 