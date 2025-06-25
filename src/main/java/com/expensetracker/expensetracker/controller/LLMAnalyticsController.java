package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.LLMAnalyticsResponse;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.service.LLMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/llm-analytics")
public class LLMAnalyticsController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LLMService llmService;

    /**
     * Get unified spending insights (narrative + visual data)
     *
     * @param userId User ID
     * @param start  (optional) Start date
     * @param end    (optional) End date
     * @return LLMAnalyticsResponse with llmText and visualData
     */
    @GetMapping("/spending-insights/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<LLMAnalyticsResponse> getSpendingInsights(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        // Fetch transactions
        List<Transaction> transactions = (start != null && end != null)
                ? transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, start, end)
                : transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);

        // Compute visual data in Java
        Map<String, Object> visualData = new HashMap<>();
        Map<String, Double> categoryBreakdown = new HashMap<>();
        double totalSpent = 0.0;
        for (Transaction t : transactions) {
            if (t.getTransactionType() == Transaction.TransactionType.EXPENSE) {
                String cat = t.getCategory() != null ? t.getCategory().getName() : "Other";
                double amt = t.getAmount() != null ? t.getAmount().doubleValue() : 0.0;
                categoryBreakdown.put(cat, categoryBreakdown.getOrDefault(cat, 0.0) + amt);
                totalSpent += amt;
            }
        }
        visualData.put("categoryBreakdown", categoryBreakdown);
        visualData.put("totalSpent", totalSpent);
        visualData.put("transactionCount", transactions.size());

        // Prepare userData for LLM
        Map<String, Object> userData = new HashMap<>();
        userData.put("totalSpent", totalSpent);
        userData.put("categoryBreakdown", categoryBreakdown);
        userData.put("transactionCount", transactions.size());

        // Get LLM narrative
        String llmText = llmService.generateSpendingInsights(userData);

        // Unified response
        LLMAnalyticsResponse response = new LLMAnalyticsResponse(llmText, visualData);
        return ResponseEntity.ok(response);
    }
} 