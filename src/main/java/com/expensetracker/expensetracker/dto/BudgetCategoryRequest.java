package com.expensetracker.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetCategoryRequest {
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotNull(message = "Budget amount is required")
    @DecimalMin(value = "0.01", message = "Budget amount must be greater than 0")
    private BigDecimal budgetAmount;
    
    private Double budgetPercentage;
} 