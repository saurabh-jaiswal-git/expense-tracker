package com.expensetracker.expensetracker.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
public class BudgetRequest {
    
    @NotNull(message = "YearMonth is required")
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth yearMonth;
    
    @NotNull(message = "Total budget is required")
    @DecimalMin(value = "0.01", message = "Total budget must be greater than 0")
    private BigDecimal totalBudget;
    
    private String notes;
} 