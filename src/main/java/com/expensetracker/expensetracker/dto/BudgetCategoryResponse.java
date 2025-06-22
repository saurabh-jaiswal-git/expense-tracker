package com.expensetracker.expensetracker.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetCategoryResponse {
    
    private Long id;
    
    private Long categoryId;
    
    private String categoryName;
    
    private String categoryDescription;
    
    private BigDecimal budgetAmount;
    
    private Double budgetPercentage;
    
    private BigDecimal actualAmount;
    
    private Double actualPercentage;
    
    private BigDecimal remainingAmount;
    
    private Double spendingPercentage;
    
    private Boolean isOverBudget;
} 