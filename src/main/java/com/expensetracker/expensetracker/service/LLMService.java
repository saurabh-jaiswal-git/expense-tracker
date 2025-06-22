package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Transaction;

import java.util.List;
import java.util.Map;

/**
 * Core LLM service interface for AI-powered financial analysis
 * This service abstracts the interaction with different LLM providers
 */
public interface LLMService {
    
    /**
     * Generate analysis based on a prompt and context
     * @param prompt The prompt to send to the LLM
     * @param context Additional context data
     * @return Generated analysis text
     */
    String generateAnalysis(String prompt, Map<String, Object> context);
    
    /**
     * Generate personalized recommendations for a user
     * @param userContext User-specific context and spending data
     * @return List of recommendation strings
     */
    List<String> generateRecommendations(String userContext);
    
    /**
     * Analyze spending patterns from transaction data
     * @param transactions List of user transactions
     * @return Analysis results as a map
     */
    Map<String, Object> analyzeSpendingPatterns(List<Map<String, Object>> transactions);
    
    /**
     * Categorize a transaction based on its description
     * @param description Transaction description
     * @param categories Available categories
     * @return Suggested category name
     */
    String categorizeTransaction(String description, List<Category> categories);
    
    /**
     * Generate budget recommendations
     * @param userIncome Monthly income
     * @param currentSpending Current spending data
     * @param goals Financial goals
     * @return Budget recommendation text
     */
    String generateBudgetRecommendation(Double userIncome, Map<String, Double> currentSpending, List<String> goals);
    
    /**
     * Detect spending anomalies
     * @param transactions Recent transactions
     * @param historicalPatterns Historical spending patterns
     * @return Anomaly detection results
     */
    Map<String, Object> detectAnomalies(List<Transaction> transactions, Map<String, Object> historicalPatterns);
    
    /**
     * Generate savings opportunities analysis
     * @param spendingData Current spending breakdown
     * @param userProfile User financial profile
     * @return Savings opportunities analysis
     */
    String generateSavingsOpportunities(Map<String, Double> spendingData, Map<String, Object> userProfile);
    
    /**
     * Get the current LLM provider being used
     * @return Provider name (e.g., "openai", "anthropic", "local")
     */
    String getProvider();
    
    /**
     * Get the current model being used
     * @return Model name (e.g., "gpt-4", "claude-3", "llama-2")
     */
    String getModel();
    
    /**
     * Check if the LLM service is available and configured
     * @return true if service is available
     */
    boolean isAvailable();
} 