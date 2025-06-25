package com.expensetracker.expensetracker.controller;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SpendingAnalysisRequest {
    private Long userId;
    private List<Map<String, Object>> transactions;
} 