package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.*;
import com.expensetracker.expensetracker.entity.*;
import com.expensetracker.expensetracker.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
@Slf4j
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Create a new budget
     */
    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(
            @RequestParam Long userId,
            @Valid @RequestBody BudgetRequest request) {
        log.info("Creating budget for user {} for month {}", userId, request.getYearMonth());
        Budget budget = budgetService.createBudget(
                userId, 
                request.getYearMonth(), 
                request.getTotalBudget(), 
                request.getNotes()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(budget));
    }

    /**
     * Get budget by ID
     */
    @GetMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> getBudget(@PathVariable Long budgetId) {
        log.info("Getting budget with id: {}", budgetId);
        return budgetService.getBudgetById(budgetId)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));
    }

    /**
     * Get budget by user and month
     */
    @GetMapping("/user/{userId}/month/{yearMonth}")
    public ResponseEntity<BudgetResponse> getBudgetByUserAndMonth(
            @PathVariable Long userId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        log.info("Getting budget for user {} and month {}", userId, yearMonth);
        return budgetService.getBudgetByUserAndMonth(userId, yearMonth)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found for user " + userId + " and month " + yearMonth));
    }

    /**
     * Get all budgets for a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetResponse>> getAllBudgetsByUser(@PathVariable Long userId) {
        log.info("Getting all budgets for user: {}", userId);
        
        List<BudgetResponse> budgets = budgetService.getAllBudgetsByUser(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(budgets);
    }

    /**
     * Update an existing budget
     */
    @PutMapping("/{budgetId}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetUpdateRequest request) {
        log.info("Updating budget: {}", budgetId);
        Budget budget = budgetService.updateBudget(
                budgetId, 
                request.getTotalBudget(), 
                request.getNotes()
        );
        return ResponseEntity.ok(convertToResponse(budget));
    }

    /**
     * Delete a budget
     */
    @DeleteMapping("/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long budgetId) {
        log.info("Deleting budget: {}", budgetId);
        budgetService.deleteBudget(budgetId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add a category to a budget
     */
    @PostMapping("/{budgetId}/categories")
    public ResponseEntity<BudgetCategoryResponse> addBudgetCategory(
            @PathVariable Long budgetId,
            @Valid @RequestBody BudgetCategoryRequest request) {
        log.info("Adding category {} to budget {} with amount {}", 
                request.getCategoryId(), budgetId, request.getBudgetAmount());
        BudgetCategory budgetCategory = budgetService.addBudgetCategory(
                budgetId,
                request.getCategoryId(),
                request.getBudgetAmount(),
                request.getBudgetPercentage()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToCategoryResponse(budgetCategory));
    }

    /**
     * Get all categories for a budget
     */
    @GetMapping("/{budgetId}/categories")
    public ResponseEntity<List<BudgetCategoryResponse>> getBudgetCategories(@PathVariable Long budgetId) {
        log.info("Getting categories for budget: {}", budgetId);
        
        List<BudgetCategoryResponse> categories = budgetService.getBudgetCategories(budgetId)
                .stream()
                .map(this::convertToCategoryResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(categories);
    }

    /**
     * Update actual spending for a budget
     */
    @PutMapping("/{budgetId}/spending")
    public ResponseEntity<BudgetResponse> updateBudgetSpending(
            @PathVariable Long budgetId,
            @RequestParam BigDecimal actualSpending) {
        log.info("Updating actual spending for budget {} to {}", budgetId, actualSpending);
        Budget budget = budgetService.updateBudgetActualSpending(budgetId, actualSpending);
        return ResponseEntity.ok(convertToResponse(budget));
    }

    /**
     * Get budget status
     */
    @GetMapping("/{budgetId}/status")
    public ResponseEntity<BudgetStatus> getBudgetStatus(@PathVariable Long budgetId) {
        log.info("Getting status for budget: {}", budgetId);
        BudgetStatus status = budgetService.getBudgetStatus(budgetId);
        return ResponseEntity.ok(status);
    }

    /**
     * Get over-budget budgets for a user
     */
    @GetMapping("/user/{userId}/over-budget")
    public ResponseEntity<List<BudgetResponse>> getOverBudgetBudgets(@PathVariable Long userId) {
        log.info("Getting over-budget budgets for user: {}", userId);
        
        List<BudgetResponse> budgets = budgetService.getOverBudgetBudgets(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get active budgets for a user
     */
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<BudgetResponse>> getActiveBudgets(@PathVariable Long userId) {
        log.info("Getting active budgets for user: {}", userId);
        
        List<BudgetResponse> budgets = budgetService.getActiveBudgets(userId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(budgets);
    }

    /**
     * Count active budgets for a user
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countActiveBudgets(@PathVariable Long userId) {
        log.info("Counting active budgets for user: {}", userId);
        
        long count = budgetService.countActiveBudgets(userId);
        return ResponseEntity.ok(count);
    }

    // Helper methods for conversion
    private BudgetResponse convertToResponse(Budget budget) {
        if (budget == null) {
            throw new IllegalArgumentException("Budget is null");
        }
        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setUserId(budget.getUser().getId());
        response.setYearMonth(budget.getYearMonth());
        response.setTotalBudget(budget.getTotalBudget());
        response.setActualSpending(budget.getActualSpending());
        response.setRemainingBudget(budget.getRemainingBudget());
        response.setSpendingPercentage(budget.getSpendingPercentage());
        response.setStatus(budget.getStatus());
        response.setIsActive(budget.getIsActive());
        response.setNotes(budget.getNotes());
        response.setCreatedAt(budget.getCreatedAt() != null ? budget.getCreatedAt().toString() : null);
        response.setUpdatedAt(budget.getUpdatedAt() != null ? budget.getUpdatedAt().toString() : null);
        
        // Convert categories if available
        if (budget.getBudgetCategories() != null && !budget.getBudgetCategories().isEmpty()) {
            List<BudgetCategoryResponse> categories = budget.getBudgetCategories()
                    .stream()
                    .map(this::convertToCategoryResponse)
                    .collect(Collectors.toList());
            response.setCategories(categories);
        }
        
        return response;
    }

    private BudgetCategoryResponse convertToCategoryResponse(BudgetCategory budgetCategory) {
        if (budgetCategory == null) {
            throw new IllegalArgumentException("BudgetCategory is null");
        }
        BudgetCategoryResponse response = new BudgetCategoryResponse();
        response.setId(budgetCategory.getId());
        response.setCategoryId(budgetCategory.getCategory().getId());
        response.setCategoryName(budgetCategory.getCategory().getName());
        response.setCategoryDescription(budgetCategory.getCategory().getDescription());
        response.setBudgetAmount(budgetCategory.getBudgetAmount());
        response.setBudgetPercentage(budgetCategory.getBudgetPercentage());
        response.setActualAmount(budgetCategory.getActualAmount());
        response.setActualPercentage(budgetCategory.getActualPercentage());
        response.setRemainingAmount(budgetCategory.getRemainingAmount());
        response.setSpendingPercentage(budgetCategory.getSpendingPercentage());
        response.setIsOverBudget(budgetCategory.isOverBudget());
        
        return response;
    }
} 