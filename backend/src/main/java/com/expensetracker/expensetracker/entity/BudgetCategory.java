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

@Entity
@Table(name = "budget_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BudgetCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id", nullable = false)
    private Budget budget;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "budget_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal budgetAmount;
    
    @Column(name = "budget_percentage")
    private Double budgetPercentage;
    
    @Column(name = "actual_amount", precision = 12, scale = 2)
    private BigDecimal actualAmount = BigDecimal.ZERO;
    
    @Column(name = "actual_percentage")
    private Double actualPercentage = 0.0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Business logic methods
    public Double calculatePercentage(BigDecimal totalBudget) {
        if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return budgetAmount.divide(totalBudget, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }
    
    public BigDecimal calculateAmount(BigDecimal totalBudget) {
        if (totalBudget == null || budgetPercentage == null) {
            return BigDecimal.ZERO;
        }
        return totalBudget.multiply(new BigDecimal(budgetPercentage))
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getRemainingAmount() {
        return budgetAmount.subtract(actualAmount != null ? actualAmount : BigDecimal.ZERO);
    }
    
    public Double getSpendingPercentage() {
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return actualAmount.divide(budgetAmount, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .doubleValue();
    }
    
    public boolean isOverBudget() {
        return actualAmount != null && budgetAmount != null && 
               actualAmount.compareTo(budgetAmount) > 0;
    }
    
    public boolean isUnderBudget() {
        return actualAmount != null && budgetAmount != null && 
               actualAmount.compareTo(budgetAmount) < 0;
    }
    
    // Validation
    public void validate() {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        if (budgetAmount == null || budgetAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget amount cannot be null or negative");
        }
        if (budgetPercentage != null && budgetPercentage < 0) {
            throw new IllegalArgumentException("Budget percentage cannot be negative");
        }
    }
} 