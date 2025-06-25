package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DataStrategyServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LLMService llmService;
    @InjectMocks
    private DataStrategyService dataStrategyService;

    private final Long userId = 1L;
    private final LocalDate startDate = LocalDate.of(2024, 1, 1);
    private final LocalDate endDate = LocalDate.of(2024, 12, 31);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Strategy Selection Logic")
    class StrategySelection {
        @Test
        void selectsRawDataStrategyForSmallDataset() {
            when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(50L);
            when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
            when(llmService.generateSpendingInsights(any())).thenReturn("raw-data-insight");

            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            assertThat(result.getStrategy()).isEqualTo(DataStrategyService.DataStrategy.RAW_DATA);
            assertThat(result.getInsights()).isEqualTo("raw-data-insight");
        }

        @Test
        void selectsIntelligentSummaryStrategyForMediumDataset() {
            when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(500L);
            when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
            when(llmService.generateSpendingInsights(any())).thenReturn("summary-insight");

            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            assertThat(result.getStrategy()).isEqualTo(DataStrategyService.DataStrategy.INTELLIGENT_SUMMARY);
            assertThat(result.getInsights()).isEqualTo("summary-insight");
        }

        @Test
        void selectsChunkedProcessingStrategyForLargeDataset() {
            when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(2000L);
            when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
            when(llmService.generateSpendingInsights(any())).thenReturn("chunked-insight");

            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            assertThat(result.getStrategy()).isEqualTo(DataStrategyService.DataStrategy.CHUNKED_PROCESSING);
            assertThat(result.getInsights()).isEqualTo("chunked-insight");
        }

        @Test
        void selectsRawDataStrategyForExactlyThreshold() {
            when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(99L);
            when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
            when(llmService.generateSpendingInsights(any())).thenReturn("raw-data-insight");

            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            assertThat(result.getStrategy()).isEqualTo(DataStrategyService.DataStrategy.RAW_DATA);
        }

        @Test
        void selectsIntelligentSummaryStrategyForExactly100() {
            when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(100L);
            when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
            when(llmService.generateSpendingInsights(any())).thenReturn("summary-insight");

            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            assertThat(result.getStrategy()).isEqualTo(DataStrategyService.DataStrategy.INTELLIGENT_SUMMARY);
        }

        @Test
        void selectsChunkedProcessingStrategyForExactly1000() {
            when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(1000L);
            when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
            when(llmService.generateSpendingInsights(any())).thenReturn("chunked-insight");

            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            assertThat(result.getStrategy()).isEqualTo(DataStrategyService.DataStrategy.CHUNKED_PROCESSING);
        }
    }

    @Test
    void testMetadataAndTransactionCountInResult() {
        when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(42L);
        when(transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate)).thenReturn(Collections.emptyList());
        when(llmService.generateSpendingInsights(any())).thenReturn("insight");

        DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
        assertThat(result.getTransactionCount()).isEqualTo(42L);
        assertThat(result.getMetadata()).containsKey("strategy");
        assertThat(result.getMetadata()).containsKey("transactionCount");
    }

    @Test
    void testGetStrategyRecommendations() {
        when(transactionRepository.countByUserIdAndTransactionDateBetween(userId, startDate, endDate)).thenReturn(500L);
        Map<String, Object> recommendations = dataStrategyService.getStrategyRecommendations(userId, startDate, endDate);
        assertThat(recommendations.get("recommendedStrategy")).isEqualTo("INTELLIGENT_SUMMARY");
        assertThat(recommendations.get("transactionCount")).isEqualTo(500L);
        assertThat(recommendations.get("reasoning")).isInstanceOf(String.class);
        assertThat(recommendations.get("estimatedPerformance")).isInstanceOf(Map.class);
    }

    // === Real-world analytics tests ===
    @Test
    void testIntelligentSummaryWithRealisticTransactions() {
        DataStrategyService service = new DataStrategyService();
        // Create realistic transactions
        Transaction t1 = new Transaction();
        t1.setId(1L);
        t1.setAmount(new BigDecimal("100.00"));
        t1.setDescription("Groceries");
        t1.setTransactionDate(LocalDate.of(2024, 1, 5));
        t1.setTransactionType(Transaction.TransactionType.EXPENSE);
        Category food = new Category();
        food.setName("Food");
        t1.setCategory(food);

        Transaction t2 = new Transaction();
        t2.setId(2L);
        t2.setAmount(new BigDecimal("1200.00"));
        t2.setDescription("Salary");
        t2.setTransactionDate(LocalDate.of(2024, 1, 1));
        t2.setTransactionType(Transaction.TransactionType.INCOME);
        t2.setCategory(null);

        Transaction t3 = new Transaction();
        t3.setId(3L);
        t3.setAmount(new BigDecimal("500.00"));
        t3.setDescription("Rent");
        t3.setTransactionDate(LocalDate.of(2024, 1, 3));
        t3.setTransactionType(Transaction.TransactionType.EXPENSE);
        Category housing = new Category();
        housing.setName("Housing");
        t3.setCategory(housing);

        Transaction t4 = new Transaction();
        t4.setId(4L);
        t4.setAmount(new BigDecimal("50.00"));
        t4.setDescription("Dining");
        t4.setTransactionDate(LocalDate.of(2024, 1, 10));
        t4.setTransactionType(Transaction.TransactionType.EXPENSE);
        t4.setCategory(food);

        List<Transaction> transactions = List.of(t1, t2, t3, t4);
        Map<String, Object> summary = service.createIntelligentSummary(transactions);
        assertThat(summary.get("totalSpent")).isEqualTo(650.0);
        assertThat(summary.get("totalIncome")).isEqualTo(1200.0);
        assertThat(summary.get("netAmount")).isEqualTo(550.0);
        Map<String, Double> categoryBreakdown = (Map<String, Double>) summary.get("categoryBreakdown");
        assertThat(categoryBreakdown.get("Food")).isEqualTo(150.0);
        assertThat(categoryBreakdown.get("Housing")).isEqualTo(500.0);
        Map<String, Double> periodBreakdown = (Map<String, Double>) summary.get("periodBreakdown");
        assertThat(periodBreakdown.get("2024-01")).isEqualTo(650.0);
    }

    @Test
    void testIntelligentSummaryWithNoTransactions() {
        DataStrategyService service = new DataStrategyService();
        Map<String, Object> summary = service.createIntelligentSummary(Collections.emptyList());
        assertThat(summary.get("totalSpent")).isEqualTo(0.0);
        assertThat(summary.get("totalIncome")).isEqualTo(0.0);
        assertThat(summary.get("netAmount")).isEqualTo(0.0);
        assertThat(((Map<?, ?>)summary.get("categoryBreakdown")).isEmpty()).isTrue();
        assertThat(((Map<?, ?>)summary.get("periodBreakdown")).isEmpty()).isTrue();
    }
} 