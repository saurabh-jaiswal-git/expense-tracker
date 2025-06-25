package com.expensetracker.expensetracker.dto;

import java.util.Map;

/**
 * Unified response for LLM-powered analytics endpoints.
 *
 * <p>Contract:
 * <ul>
 *   <li><b>llmText</b>: Language-generated insights, summary, or advice for the user.</li>
 *   <li><b>visualData</b>: Structured data for frontend visualizations (e.g., category breakdown, trends, summary stats).</li>
 * </ul>
 *
 * <p>Example:
 * <pre>
 * {
 *   "llmText": "You spent 40% of your income on food. Consider cooking at home more often.",
 *   "visualData": {
 *     "categoryBreakdown": { "Food": 400, "Transport": 200 },
 *     "trend": [ ... ],
 *     "summary": { ... }
 *   }
 * }
 * </pre>
 */
public class LLMAnalyticsResponse {
    private String llmText;
    private Map<String, Object> visualData;

    public LLMAnalyticsResponse() {}

    public LLMAnalyticsResponse(String llmText, Map<String, Object> visualData) {
        this.llmText = llmText;
        this.visualData = visualData;
    }

    public String getLlmText() {
        return llmText;
    }

    public void setLlmText(String llmText) {
        this.llmText = llmText;
    }

    public Map<String, Object> getVisualData() {
        return visualData;
    }

    public void setVisualData(Map<String, Object> visualData) {
        this.visualData = visualData;
    }
} 