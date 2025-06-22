package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    /**
     * Find all budgets for a specific user
     */
    List<Budget> findByUserIdOrderByYearMonthDesc(Long userId);
    
    /**
     * Find budget for a specific user and month
     */
    Optional<Budget> findByUserIdAndYearMonth(Long userId, YearMonth yearMonth);
    
    /**
     * Find active budgets for a specific user
     */
    List<Budget> findByUserIdAndIsActiveTrueOrderByYearMonthDesc(Long userId);
    
    /**
     * Find budgets for a specific user within a date range
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.yearMonth BETWEEN :startMonth AND :endMonth ORDER BY b.yearMonth DESC")
    List<Budget> findByUserIdAndYearMonthBetween(@Param("userId") Long userId, 
                                                @Param("startMonth") YearMonth startMonth, 
                                                @Param("endMonth") YearMonth endMonth);
    
    /**
     * Find current month budget for a user
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.yearMonth = :currentMonth")
    Optional<Budget> findCurrentMonthBudget(@Param("userId") Long userId, 
                                           @Param("currentMonth") YearMonth currentMonth);
    
    /**
     * Check if a budget exists for a user and month
     */
    boolean existsByUserIdAndYearMonth(Long userId, YearMonth yearMonth);
    
    /**
     * Find budgets that are over budget
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.actualSpending > b.totalBudget")
    List<Budget> findOverBudgetBudgets(@Param("userId") Long userId);
    
    /**
     * Find budgets that are under budget
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.actualSpending < b.totalBudget")
    List<Budget> findUnderBudgetBudgets(@Param("userId") Long userId);
    
    /**
     * Count active budgets for a user
     */
    long countByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find the most recent budget for a user
     */
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId ORDER BY b.yearMonth DESC LIMIT 1")
    Optional<Budget> findMostRecentBudget(@Param("userId") Long userId);
} 