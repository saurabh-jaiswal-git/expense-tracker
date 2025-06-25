package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.BudgetStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
public class BudgetResponse {
    
    private Long id;
    
    private Long userId;
    
    @JsonFormat(pattern = "yyyy-MM")
    private YearMonth yearMonth;
    
    private BigDecimal totalBudget;
    
    private BigDecimal actualSpending;
    
    private BigDecimal remainingBudget;
    
    private Double spendingPercentage;
    
    private BudgetStatus status;
    
    private Boolean isActive;
    
    private String notes;
    
    private List<BudgetCategoryResponse> categories;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String updatedAt;
} 