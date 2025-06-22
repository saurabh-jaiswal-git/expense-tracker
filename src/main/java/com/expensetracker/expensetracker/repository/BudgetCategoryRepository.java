package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.BudgetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
    
    /**
     * Find all budget categories for a specific budget
     */
    List<BudgetCategory> findByBudgetIdOrderByBudgetAmountDesc(Long budgetId);
    
    /**
     * Find budget category by budget and category
     */
    Optional<BudgetCategory> findByBudgetIdAndCategoryId(Long budgetId, Long categoryId);
    
    /**
     * Find budget categories that are over budget
     */
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.budget.id = :budgetId AND bc.actualAmount > bc.budgetAmount")
    List<BudgetCategory> findOverBudgetCategories(@Param("budgetId") Long budgetId);
    
    /**
     * Find budget categories that are under budget
     */
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.budget.id = :budgetId AND bc.actualAmount < bc.budgetAmount")
    List<BudgetCategory> findUnderBudgetCategories(@Param("budgetId") Long budgetId);
    
    /**
     * Find budget categories by category across all budgets for a user
     */
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.budget.user.id = :userId AND bc.category.id = :categoryId ORDER BY bc.budget.yearMonth DESC")
    List<BudgetCategory> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
    
    /**
     * Calculate total actual spending for a budget
     */
    @Query("SELECT COALESCE(SUM(bc.actualAmount), 0) FROM BudgetCategory bc WHERE bc.budget.id = :budgetId")
    BigDecimal calculateTotalActualSpending(@Param("budgetId") Long budgetId);
    
    /**
     * Calculate total budgeted amount for a budget
     */
    @Query("SELECT COALESCE(SUM(bc.budgetAmount), 0) FROM BudgetCategory bc WHERE bc.budget.id = :budgetId")
    BigDecimal calculateTotalBudgetedAmount(@Param("budgetId") Long budgetId);
    
    /**
     * Find budget categories with highest spending percentage
     */
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.budget.id = :budgetId ORDER BY (bc.actualAmount / bc.budgetAmount) DESC")
    List<BudgetCategory> findCategoriesBySpendingPercentage(@Param("budgetId") Long budgetId);
    
    /**
     * Find budget categories with lowest remaining amount
     */
    @Query("SELECT bc FROM BudgetCategory bc WHERE bc.budget.id = :budgetId ORDER BY (bc.budgetAmount - bc.actualAmount) ASC")
    List<BudgetCategory> findCategoriesByRemainingAmount(@Param("budgetId") Long budgetId);
    
    /**
     * Delete all budget categories for a specific budget
     */
    void deleteByBudgetId(Long budgetId);
    
    /**
     * Check if a budget category exists for a budget and category
     */
    boolean existsByBudgetIdAndCategoryId(Long budgetId, Long categoryId);
} 