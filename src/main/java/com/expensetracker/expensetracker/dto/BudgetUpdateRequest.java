package com.expensetracker.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetUpdateRequest {
    
    @DecimalMin(value = "0.01", message = "Total budget must be greater than 0")
    private BigDecimal totalBudget;
    
    private String notes;
} 