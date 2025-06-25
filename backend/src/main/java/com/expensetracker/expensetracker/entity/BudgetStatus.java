package com.expensetracker.expensetracker.entity;

public enum BudgetStatus {
    ON_TRACK,      // Spending is within budget
    UNDER_BUDGET,  // Spending is below budget
    OVER_BUDGET,   // Spending exceeds budget
    AT_LIMIT       // Spending is at or near budget limit (within 5% tolerance)
} 