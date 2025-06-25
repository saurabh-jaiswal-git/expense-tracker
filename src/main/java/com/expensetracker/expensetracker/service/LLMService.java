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
    
    // ========================================================================
    // ANALYTICS-SPECIFIC METHODS
    // ========================================================================
    
    /**
     * Generate comprehensive spending insights for a user
     * @param userData Complete user financial data (transactions, budgets, goals)
     * @return Detailed spending insights and analysis
     */
    String generateSpendingInsights(Map<String, Object> userData);
    
    /**
     * Generate category-wise spending analysis
     * @param categoryBreakdown Category spending data
     * @param userProfile User information
     * @return Category-specific insights and recommendations
     */
    String generateCategoryAnalysis(Map<String, Object> categoryBreakdown, Map<String, Object> userProfile);
    
    /**
     * Generate budget vs actual spending analysis
     * @param budgetData Budget information
     * @param actualSpending Actual spending data
     * @return Budget performance analysis
     */
    String generateBudgetAnalysis(Map<String, Object> budgetData, Map<String, Object> actualSpending);
    
    /**
     * Generate goal progress analysis and recommendations
     * @param goals User's financial goals
     * @param spendingData Current spending patterns
     * @return Goal progress insights and recommendations
     */
    String generateGoalAnalysis(List<Map<String, Object>> goals, Map<String, Object> spendingData);
    
    /**
     * Generate spending trends analysis
     * @param spendingTrends Historical spending data by period
     * @param userProfile User information
     * @return Trend analysis and predictions
     */
    String generateTrendAnalysis(Map<String, Object> spendingTrends, Map<String, Object> userProfile);
    
    /**
     * Generate personalized financial recommendations
     * @param userData Complete user financial data
     * @param analysisType Type of analysis (spending, saving, investing, etc.)
     * @return Personalized recommendations
     */
    String generatePersonalizedRecommendations(Map<String, Object> userData, String analysisType);
    
    /**
     * Generate spending alerts and warnings
     * @param userData User financial data
     * @param thresholds Alert thresholds
     * @return Spending alerts and warnings
     */
    String generateSpendingAlerts(Map<String, Object> userData, Map<String, Object> thresholds);
    
    /**
     * Generate monthly financial summary
     * @param monthlyData Monthly financial data
     * @param userProfile User information
     * @return Monthly summary and insights
     */
    String generateMonthlySummary(Map<String, Object> monthlyData, Map<String, Object> userProfile);
    
    /**
     * Generate conversational financial advice
     * @param userData User financial data
     * @param userQuestion User's specific question
     * @return Conversational financial advice
     */
    String generateConversationalAdvice(Map<String, Object> userData, String userQuestion);
    
    /**
     * Generate spending optimization suggestions
     * @param userData User financial data
     * @param optimizationGoal Goal for optimization (save money, reduce debt, etc.)
     * @return Optimization suggestions
     */
    String generateOptimizationSuggestions(Map<String, Object> userData, String optimizationGoal);
    
    /**
     * Generate financial health score and analysis
     * @param userData User financial data
     * @return Financial health score and detailed analysis
     */
    Map<String, Object> generateFinancialHealthScore(Map<String, Object> userData);
    
    /**
     * Generate comparison analysis (month-over-month, year-over-year)
     * @param currentData Current period data
     * @param previousData Previous period data
     * @param comparisonType Type of comparison
     * @return Comparison analysis
     */
    String generateComparisonAnalysis(Map<String, Object> currentData, Map<String, Object> previousData, String comparisonType);
    
    /**
     * Generate actionable next steps for financial improvement
     * @param userData User financial data
     * @param priority Priority level (high, medium, low)
     * @return Actionable next steps
     */
    String generateActionableSteps(Map<String, Object> userData, String priority);
    
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