package com.expensetracker.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Budget {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "year_month", nullable = false)
    private YearMonth yearMonth;
    
    @Column(name = "total_budget", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalBudget;
    
    @Column(name = "actual_spending", precision = 12, scale = 2)
    private BigDecimal actualSpending = BigDecimal.ZERO;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "notes")
    private String notes;
    
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BudgetCategory> budgetCategories = new ArrayList<>();
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Business logic methods
    public BudgetStatus getStatus() {
        if (actualSpending == null || totalBudget == null) {
            return BudgetStatus.ON_TRACK;
        }
        
        BigDecimal spendingPercentage = actualSpending.divide(totalBudget, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        
        if (spendingPercentage.compareTo(new BigDecimal("95")) <= 0) {
            return BudgetStatus.UNDER_BUDGET;
        } else if (spendingPercentage.compareTo(new BigDecimal("105")) >= 0) {
            return BudgetStatus.OVER_BUDGET;
        } else {
            return BudgetStatus.AT_LIMIT;
        }
    }
    
    public BigDecimal getRemainingBudget() {
        if (totalBudget == null || actualSpending == null) {
            return totalBudget != null ? totalBudget : BigDecimal.ZERO;
        }
        return totalBudget.subtract(actualSpending);
    }
    
    public Double getSpendingPercentage() {
        if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return actualSpending.divide(totalBudget, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }
    
    public boolean isOverBudget() {
        return actualSpending != null && totalBudget != null && 
               actualSpending.compareTo(totalBudget) > 0;
    }
    
    public boolean isUnderBudget() {
        return actualSpending != null && totalBudget != null && 
               actualSpending.compareTo(totalBudget) < 0;
    }
    
    public boolean isCurrentMonth() {
        return yearMonth != null && yearMonth.equals(YearMonth.now());
    }
    
    public boolean isFutureMonth() {
        return yearMonth != null && yearMonth.isAfter(YearMonth.now());
    }
    
    public boolean isPastMonth() {
        return yearMonth != null && yearMonth.isBefore(YearMonth.now());
    }
    
    public boolean isWithinOneYear() {
        if (yearMonth == null) {
            return false;
        }
        YearMonth now = YearMonth.now();
        YearMonth oneYearAgo = now.minusMonths(12);
        YearMonth oneYearFromNow = now.plusMonths(12);
        // Inclusive of the boundary months
        return (yearMonth.equals(oneYearAgo) || yearMonth.isAfter(oneYearAgo)) &&
               (yearMonth.equals(oneYearFromNow) || yearMonth.isBefore(oneYearFromNow));
    }
    
    // Helper methods for budget categories
    public void addBudgetCategory(BudgetCategory budgetCategory) {
        budgetCategories.add(budgetCategory);
        budgetCategory.setBudget(this);
    }
    
    public void removeBudgetCategory(BudgetCategory budgetCategory) {
        budgetCategories.remove(budgetCategory);
        budgetCategory.setBudget(null);
    }
    
    public BigDecimal getTotalBudgetedAmount() {
        return budgetCategories.stream()
                .map(BudgetCategory::getBudgetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal getTotalActualAmount() {
        return budgetCategories.stream()
                .map(BudgetCategory::getActualAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Validation
    public void validate() {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (yearMonth == null) {
            throw new IllegalArgumentException("YearMonth cannot be null");
        }
        if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Total budget cannot be null or negative");
        }
        if (!isWithinOneYear()) {
            throw new IllegalArgumentException("Budget can only be created for periods within one year");
        }
    }
    
    // Pre-persist validation
    @PrePersist
    @PreUpdate
    public void validateBeforePersist() {
        validate();
    }
} 