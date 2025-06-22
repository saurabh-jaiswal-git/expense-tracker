package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.*;
import com.expensetracker.expensetracker.repository.BudgetCategoryRepository;
import com.expensetracker.expensetracker.repository.BudgetRepository;
import com.expensetracker.expensetracker.repository.CategoryRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // Constants for validation
    private static final BigDecimal MAX_BUDGET_AMOUNT = new BigDecimal("999999.99");
    private static final BigDecimal MIN_BUDGET_AMOUNT = new BigDecimal("0.01");
    private static final int MAX_NOTES_LENGTH = 1000;
    private static final double MAX_PERCENTAGE = 100.0;
    private static final double MIN_PERCENTAGE = 0.0;

    /**
     * Create a new budget for a user and month
     */
    public Budget createBudget(Long userId, YearMonth yearMonth, BigDecimal totalBudget, String notes) {
        log.info("Creating budget for user {} for month {}", userId, yearMonth);
        
        // Input validation
        validateUserId(userId);
        validateBudgetAmount(totalBudget);
        validateNotes(notes);
        validateBudgetPeriod(yearMonth);
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Check for duplicate budget
        if (budgetRepository.existsByUserIdAndYearMonth(userId, yearMonth)) {
            throw new IllegalArgumentException("Budget already exists for user " + userId + " and month " + yearMonth);
        }
        
        Budget budget = Budget.builder()
                .user(user)
                .yearMonth(yearMonth)
                .totalBudget(totalBudget)
                .actualSpending(BigDecimal.ZERO)
                .isActive(true)
                .notes(notes)
                .build();
        
        return budgetRepository.save(budget);
    }

    /**
     * Get budget by ID
     */
    @Transactional(readOnly = true)
    public Optional<Budget> getBudgetById(Long budgetId) {
        log.debug("Finding budget by id: {}", budgetId);
        validateBudgetId(budgetId);
        return budgetRepository.findById(budgetId);
    }

    /**
     * Get budget by user and month
     */
    @Transactional(readOnly = true)
    public Optional<Budget> getBudgetByUserAndMonth(Long userId, YearMonth yearMonth) {
        log.debug("Finding budget for user {} and month {}", userId, yearMonth);
        validateUserId(userId);
        validateBudgetPeriod(yearMonth);
        return budgetRepository.findByUserIdAndYearMonth(userId, yearMonth);
    }

    /**
     * Get all budgets for a user
     */
    @Transactional(readOnly = true)
    public List<Budget> getAllBudgetsByUser(Long userId) {
        log.debug("Finding all budgets for user: {}", userId);
        validateUserId(userId);
        return budgetRepository.findByUserIdOrderByYearMonthDesc(userId);
    }

    /**
     * Update an existing budget
     */
    public Budget updateBudget(Long budgetId, BigDecimal totalBudget, String notes) {
        log.info("Updating budget: {}", budgetId);
        
        validateBudgetId(budgetId);
        if (totalBudget != null) {
            validateBudgetAmount(totalBudget);
        }
        validateNotes(notes);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));
        
        if (totalBudget != null) {
            budget.setTotalBudget(totalBudget);
        }
        
        if (notes != null) {
            budget.setNotes(notes);
        }
        
        return budgetRepository.save(budget);
    }

    /**
     * Delete a budget
     */
    public void deleteBudget(Long budgetId) {
        log.info("Deleting budget: {}", budgetId);
        
        validateBudgetId(budgetId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));
        
        budgetRepository.delete(budget);
    }

    /**
     * Add a category to a budget
     */
    public BudgetCategory addBudgetCategory(Long budgetId, Long categoryId, BigDecimal budgetAmount, Double budgetPercentage) {
        log.info("Adding category {} to budget {} with amount {}", categoryId, budgetId, budgetAmount);
        
        validateBudgetId(budgetId);
        validateCategoryId(categoryId);
        validateBudgetAmount(budgetAmount);
        validateBudgetPercentage(budgetPercentage);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));
        
        // Check if category already exists in budget
        if (budgetCategoryRepository.existsByBudgetIdAndCategoryId(budgetId, categoryId)) {
            throw new IllegalArgumentException("Category " + categoryId + " already exists in budget " + budgetId);
        }
        
        // Validate that category amount doesn't exceed total budget
        if (budgetAmount.compareTo(budget.getTotalBudget()) > 0) {
            throw new IllegalArgumentException("Category budget amount cannot exceed total budget amount");
        }
        
        BudgetCategory budgetCategory = BudgetCategory.builder()
                .budget(budget)
                .category(category)
                .budgetAmount(budgetAmount)
                .budgetPercentage(budgetPercentage)
                .actualAmount(BigDecimal.ZERO)
                .actualPercentage(0.0)
                .build();
        
        return budgetCategoryRepository.save(budgetCategory);
    }

    /**
     * Get all categories for a budget
     */
    @Transactional(readOnly = true)
    public List<BudgetCategory> getBudgetCategories(Long budgetId) {
        log.debug("Finding categories for budget: {}", budgetId);
        validateBudgetId(budgetId);
        return budgetCategoryRepository.findByBudgetIdOrderByBudgetAmountDesc(budgetId);
    }

    /**
     * Update actual spending for a budget
     */
    public Budget updateBudgetActualSpending(Long budgetId, BigDecimal actualSpending) {
        log.info("Updating actual spending for budget {} to {}", budgetId, actualSpending);
        
        validateBudgetId(budgetId);
        validateSpendingAmount(actualSpending);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));
        
        budget.setActualSpending(actualSpending);
        return budgetRepository.save(budget);
    }

    /**
     * Get budget status
     */
    @Transactional(readOnly = true)
    public BudgetStatus getBudgetStatus(Long budgetId) {
        validateBudgetId(budgetId);
        
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new IllegalArgumentException("Budget not found with id: " + budgetId));
        
        return budget.getStatus();
    }

    /**
     * Get over-budget budgets for a user
     */
    @Transactional(readOnly = true)
    public List<Budget> getOverBudgetBudgets(Long userId) {
        log.debug("Finding over-budget budgets for user: {}", userId);
        validateUserId(userId);
        return budgetRepository.findOverBudgetBudgets(userId);
    }

    /**
     * Get active budgets for a user
     */
    @Transactional(readOnly = true)
    public List<Budget> getActiveBudgets(Long userId) {
        log.debug("Finding active budgets for user: {}", userId);
        validateUserId(userId);
        return budgetRepository.findByUserIdAndIsActiveTrueOrderByYearMonthDesc(userId);
    }

    /**
     * Count active budgets for a user
     */
    @Transactional(readOnly = true)
    public long countActiveBudgets(Long userId) {
        log.debug("Counting active budgets for user: {}", userId);
        validateUserId(userId);
        return budgetRepository.countByUserIdAndIsActiveTrue(userId);
    }

    // ========== VALIDATION METHODS ==========

    /**
     * Validate user ID
     */
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    /**
     * Validate budget ID
     */
    private void validateBudgetId(Long budgetId) {
        if (budgetId == null) {
            throw new IllegalArgumentException("Budget ID cannot be null");
        }
        if (budgetId <= 0) {
            throw new IllegalArgumentException("Budget ID must be positive");
        }
    }

    /**
     * Validate category ID
     */
    private void validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        if (categoryId <= 0) {
            throw new IllegalArgumentException("Category ID must be positive");
        }
    }

    /**
     * Validate budget amount
     */
    private void validateBudgetAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Budget amount cannot be null");
        }
        if (amount.compareTo(MIN_BUDGET_AMOUNT) < 0) {
            throw new IllegalArgumentException("Budget amount must be at least " + MIN_BUDGET_AMOUNT);
        }
        if (amount.compareTo(MAX_BUDGET_AMOUNT) > 0) {
            throw new IllegalArgumentException("Budget amount cannot exceed " + MAX_BUDGET_AMOUNT);
        }
    }

    /**
     * Validate spending amount
     */
    private void validateSpendingAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Spending amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Spending amount cannot be negative");
        }
        if (amount.compareTo(MAX_BUDGET_AMOUNT) > 0) {
            throw new IllegalArgumentException("Spending amount cannot exceed " + MAX_BUDGET_AMOUNT);
        }
    }

    /**
     * Validate budget percentage
     */
    private void validateBudgetPercentage(Double percentage) {
        if (percentage != null) {
            if (percentage < MIN_PERCENTAGE || percentage > MAX_PERCENTAGE) {
                throw new IllegalArgumentException("Budget percentage must be between " + MIN_PERCENTAGE + " and " + MAX_PERCENTAGE);
            }
        }
    }

    /**
     * Validate notes
     */
    private void validateNotes(String notes) {
        if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
            throw new IllegalArgumentException("Notes cannot exceed " + MAX_NOTES_LENGTH + " characters");
        }
    }

    /**
     * Validate budget period is within one year
     */
    public void validateBudgetPeriod(YearMonth yearMonth) {
        if (yearMonth == null) {
            throw new IllegalArgumentException("YearMonth cannot be null");
        }
        
        if (!yearMonth.isAfter(YearMonth.now().minusMonths(12)) || 
            !yearMonth.isBefore(YearMonth.now().plusMonths(12))) {
            throw new IllegalArgumentException("Budget can only be created for periods within one year");
        }
    }

    /**
     * Calculate budget percentage
     */
    public double calculateBudgetPercentage(BigDecimal categoryAmount, BigDecimal totalBudget) {
        if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        if (categoryAmount == null) {
            return 0.0;
        }
        
        return categoryAmount.divide(totalBudget, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }
} 