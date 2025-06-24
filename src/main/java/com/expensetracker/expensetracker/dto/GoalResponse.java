package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.GoalStatus;
import com.expensetracker.expensetracker.entity.GoalType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GoalResponse {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private GoalType goalType;
    private LocalDate targetDate;
    private LocalDate startDate;
    private GoalStatus status;
    private BigDecimal progressPercentage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 