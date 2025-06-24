package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.GoalType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalRequest {
    @NotBlank(message = "Goal name is required")
    private String name;

    private String description;

    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;

    @NotNull(message = "Goal type is required")
    private GoalType goalType;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;
} 