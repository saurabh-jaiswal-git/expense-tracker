package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenAI implementation of the LLM service
 * Handles API calls to OpenAI's GPT models for financial analysis
 */
@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "openai", matchIfMissing = true)
public class OpenAILLMService implements LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAILLMService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    @Value("${ai.llm.openai.api-key:}")
    private String apiKey;
    
    @Value("${ai.llm.openai.model:gpt-4}")
    private String model;
    
    @Value("${ai.llm.openai.max-tokens:1000}")
    private int maxTokens;
    
    @Value("${ai.llm.openai.temperature:0.7}")
    private double temperature;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public OpenAILLMService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String generateAnalysis(String prompt, Map<String, Object> context) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock analysis");
                return "Mock analysis: Based on the provided data, here are some general financial insights and recommendations.";
            }
            String enhancedPrompt = buildEnhancedPrompt(prompt, context);
            return callOpenAI(enhancedPrompt);
        } catch (Exception e) {
            logger.error("Error generating analysis: {}", e.getMessage(), e);
            return "Unable to generate analysis at this time. Please try again later.";
        }
    }
    
    @Override
    public List<String> generateRecommendations(String userContext) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock recommendations");
                return Arrays.asList(
                    "Reduce dining out expenses by cooking at home 3-4 times per week",
                    "Create an emergency fund with 10% of your monthly income",
                    "Review and cancel unused subscriptions",
                    "Set up automatic savings transfers"
                );
            }
            String prompt = buildRecommendationPrompt(userContext);
            String response = callOpenAI(prompt);
            return parseRecommendations(response);
        } catch (Exception e) {
            logger.error("Error generating recommendations: {}", e.getMessage(), e);
            return Arrays.asList("Unable to generate recommendations at this time.");
        }
    }
    
    @Override
    public Map<String, Object> analyzeSpendingPatterns(List<Map<String, Object>> transactions) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock spending analysis");
                return Map.of(
                    "totalSpending", 15000.0,
                    "topCategories", Arrays.asList("Food", "Transportation", "Entertainment"),
                    "insights", "You spend 40% of your income on food. Consider cooking at home more often to save money.",
                    "suggestions", Arrays.asList("Reduce dining out", "Create a grocery budget", "Track daily expenses")
                );
            }
            String prompt = buildSpendingAnalysisPrompt(transactions);
            String response = callOpenAI(prompt);
            return parseAnalysisResponse(response);
        } catch (Exception e) {
            logger.error("Error analyzing spending patterns: {}", e.getMessage(), e);
            return Map.of("error", "Unable to analyze spending patterns");
        }
    }
    
    @Override
    public String categorizeTransaction(String description, List<Category> categories) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock categorization");
                // Simple mock categorization based on keywords
                String lowerDesc = description.toLowerCase();
                if (lowerDesc.contains("food") || lowerDesc.contains("grocery") || lowerDesc.contains("restaurant")) {
                    return "Food";
                } else if (lowerDesc.contains("uber") || lowerDesc.contains("taxi") || lowerDesc.contains("gas")) {
                    return "Transportation";
                } else if (lowerDesc.contains("movie") || lowerDesc.contains("netflix") || lowerDesc.contains("entertainment")) {
                    return "Entertainment";
                } else {
                    return "Other";
                }
            }
            String prompt = buildCategorizationPrompt(description, categories);
            return callOpenAI(prompt).trim();
        } catch (Exception e) {
            logger.error("Error categorizing transaction: {}", e.getMessage(), e);
            return "Uncategorized";
        }
    }
    
    @Override
    public String generateBudgetRecommendation(Double userIncome, Map<String, Double> currentSpending, List<String> goals) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock budget recommendation");
                return "Based on your income of " + userIncome + " INR, consider allocating 50% to needs, 30% to wants, and 20% to savings. Focus on reducing dining out expenses and building an emergency fund.";
            }
            String prompt = buildBudgetPrompt(userIncome, currentSpending, goals);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating budget recommendation: {}", e.getMessage(), e);
            return "Unable to generate budget recommendation at this time.";
        }
    }
    
    @Override
    public Map<String, Object> detectAnomalies(List<Transaction> transactions, Map<String, Object> historicalPatterns) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock anomaly detection");
                return Map.of(
                    "anomalies", Arrays.asList(
                        Map.of(
                            "transactionId", "TXN001",
                            "description", "Unusual high spending on dining",
                            "amount", 2500.0,
                            "expectedRange", "500-1500",
                            "severity", "MEDIUM",
                            "suggestion", "Review dining out frequency"
                        )
                    ),
                    "totalAnomalies", 1,
                    "riskLevel", "LOW",
                    "insights", "One anomaly detected in recent transactions"
                );
            }
            String prompt = buildAnomalyDetectionPrompt(transactions, historicalPatterns);
            String response = callOpenAI(prompt);
            return parseAnalysisResponse(response);
        } catch (Exception e) {
            logger.error("Error detecting anomalies: {}", e.getMessage(), e);
            return Map.of("error", "Unable to detect anomalies");
        }
    }
    
    @Override
    public String generateSavingsOpportunities(Map<String, Double> spendingData, Map<String, Object> userProfile) {
        try {
            String prompt = buildSavingsPrompt(spendingData, userProfile);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating savings opportunities: {}", e.getMessage(), e);
            return "Unable to generate savings opportunities at this time.";
        }
    }
    
    @Override
    public String generateSpendingInsights(Map<String, Object> userData) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock spending insights");
                return "Based on your spending data, you're spending 40% of your income on food and dining. Consider setting a monthly dining budget and cooking at home more often to save money.";
            }
            String prompt = buildSpendingInsightsPrompt(userData);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating spending insights: {}", e.getMessage(), e);
            return "Unable to generate spending insights at this time.";
        }
    }
    
    @Override
    public String generateCategoryAnalysis(Map<String, Object> categoryBreakdown, Map<String, Object> userProfile) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock category analysis");
                return "Your top spending category is Food & Dining (40%). This is higher than the recommended 15-20%. Consider reducing dining out frequency and setting a grocery budget.";
            }
            String prompt = buildCategoryAnalysisPrompt(categoryBreakdown, userProfile);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating category analysis: {}", e.getMessage(), e);
            return "Unable to generate category analysis at this time.";
        }
    }
    
    @Override
    public String generateBudgetAnalysis(Map<String, Object> budgetData, Map<String, Object> actualSpending) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock budget analysis");
                return "You're currently 15% over your monthly budget. Your dining out expenses are the main contributor. Consider reducing restaurant visits and cooking at home more.";
            }
            String prompt = buildBudgetAnalysisPrompt(budgetData, actualSpending);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating budget analysis: {}", e.getMessage(), e);
            return "Unable to generate budget analysis at this time.";
        }
    }
    
    @Override
    public String generateGoalAnalysis(List<Map<String, Object>> goals, Map<String, Object> spendingData) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock goal analysis");
                return "You're making good progress on your emergency fund goal (60% complete). To reach your target faster, consider reducing entertainment expenses by 20%.";
            }
            String prompt = buildGoalAnalysisPrompt(goals, spendingData);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating goal analysis: {}", e.getMessage(), e);
            return "Unable to generate goal analysis at this time.";
        }
    }
    
    @Override
    public String generateTrendAnalysis(Map<String, Object> spendingTrends, Map<String, Object> userProfile) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock trend analysis");
                return "Your spending has increased by 12% compared to last month, primarily due to higher dining out expenses. This trend suggests you may need to adjust your budget.";
            }
            String prompt = buildTrendAnalysisPrompt(spendingTrends, userProfile);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating trend analysis: {}", e.getMessage(), e);
            return "Unable to generate trend analysis at this time.";
        }
    }
    
    @Override
    public String generatePersonalizedRecommendations(Map<String, Object> userData, String analysisType) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock personalized recommendations");
                return "Based on your " + analysisType + " analysis: 1) Set a monthly dining budget of 5000 INR, 2) Create an emergency fund with 10% of income, 3) Review and cancel unused subscriptions.";
            }
            String prompt = buildPersonalizedRecommendationsPrompt(userData, analysisType);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating personalized recommendations: {}", e.getMessage(), e);
            return "Unable to generate personalized recommendations at this time.";
        }
    }
    
    @Override
    public String generateSpendingAlerts(Map<String, Object> userData, Map<String, Object> thresholds) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock spending alerts");
                return "⚠️ ALERT: You've spent 85% of your monthly budget with 10 days remaining. Your dining out expenses are 40% above your target. Consider reducing restaurant visits this week.";
            }
            String prompt = buildSpendingAlertsPrompt(userData, thresholds);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating spending alerts: {}", e.getMessage(), e);
            return "Unable to generate spending alerts at this time.";
        }
    }
    
    @Override
    public String generateMonthlySummary(Map<String, Object> monthlyData, Map<String, Object> userProfile) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock monthly summary");
                return "📊 Monthly Summary: You spent 45,000 INR this month, 15% over budget. Top categories: Food (40%), Transportation (25%), Entertainment (20%). Savings rate: 15%.";
            }
            String prompt = buildMonthlySummaryPrompt(monthlyData, userProfile);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating monthly summary: {}", e.getMessage(), e);
            return "Unable to generate monthly summary at this time.";
        }
    }
    
    @Override
    public String generateConversationalAdvice(Map<String, Object> userData, String userQuestion) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock conversational advice");
                return "Based on your question: '" + userQuestion + "', here's my advice: Consider setting up automatic savings transfers and reviewing your subscription expenses monthly.";
            }
            String prompt = buildConversationalAdvicePrompt(userData, userQuestion);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating conversational advice: {}", e.getMessage(), e);
            return "Unable to generate conversational advice at this time.";
        }
    }
    
    @Override
    public String generateOptimizationSuggestions(Map<String, Object> userData, String optimizationGoal) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock optimization suggestions");
                return "To " + optimizationGoal + ": 1) Reduce dining out by 50% (save 10,000 INR/month), 2) Cancel unused subscriptions (save 2,000 INR/month), 3) Use public transport more (save 3,000 INR/month).";
            }
            String prompt = buildOptimizationSuggestionsPrompt(userData, optimizationGoal);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating optimization suggestions: {}", e.getMessage(), e);
            return "Unable to generate optimization suggestions at this time.";
        }
    }
    
    @Override
    public Map<String, Object> generateFinancialHealthScore(Map<String, Object> userData) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock financial health score");
                return Map.of(
                    "score", 72,
                    "grade", "B",
                    "analysis", "Good financial health with room for improvement",
                    "strengths", Arrays.asList("Regular income", "Some savings", "Budget awareness"),
                    "weaknesses", Arrays.asList("High dining expenses", "No emergency fund", "Limited investments"),
                    "recommendations", Arrays.asList("Build emergency fund", "Reduce dining out", "Start investing")
                );
            }
            String prompt = buildFinancialHealthScorePrompt(userData);
            String response = callOpenAI(prompt);
            return parseFinancialHealthScore(response);
        } catch (Exception e) {
            logger.error("Error generating financial health score: {}", e.getMessage(), e);
            return Map.of("error", "Unable to generate financial health score");
        }
    }
    
    @Override
    public String generateComparisonAnalysis(Map<String, Object> currentData, Map<String, Object> previousData, String comparisonType) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock comparison analysis");
                return "Month-over-month comparison: Your spending increased by 12% this month. Food expenses rose by 25%, while transportation decreased by 10%. Overall budget adherence improved by 5%.";
            }
            String prompt = buildComparisonAnalysisPrompt(currentData, previousData, comparisonType);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating comparison analysis: {}", e.getMessage(), e);
            return "Unable to generate comparison analysis at this time.";
        }
    }
    
    @Override
    public String generateActionableSteps(Map<String, Object> userData, String priority) {
        try {
            if (!isAvailable()) {
                logger.warn("OpenAI API key not configured, returning mock actionable steps");
                return "High priority actions: 1) Set up emergency fund (this week), 2) Reduce dining out by 50% (this month), 3) Review all subscriptions (this weekend).";
            }
            String prompt = buildActionableStepsPrompt(userData, priority);
            return callOpenAI(prompt);
        } catch (Exception e) {
            logger.error("Error generating actionable steps: {}", e.getMessage(), e);
            return "Unable to generate actionable steps at this time.";
        }
    }
    
    @Override
    public String getProvider() {
        return "openai";
    }
    
    @Override
    public String getModel() {
        return model;
    }
    
    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }
    
    private String callOpenAI(String prompt) throws IOException, InterruptedException {
        if (!isAvailable()) {
            throw new IllegalStateException("OpenAI API key not configured");
        }
        
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", maxTokens,
                "temperature", temperature
        );
        
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            logger.error("OpenAI API error: {} - {}", response.statusCode(), response.body());
            
            if (response.statusCode() == 429) {
                throw new RuntimeException("OpenAI API quota exceeded. Please check your billing and plan details. Error: " + response.body());
            } else if (response.statusCode() == 401) {
                throw new RuntimeException("OpenAI API authentication failed. Please check your API key. Error: " + response.body());
            } else if (response.statusCode() == 403) {
                throw new RuntimeException("OpenAI API access forbidden. Please check your API key permissions. Error: " + response.body());
            } else {
                throw new RuntimeException("OpenAI API call failed with status " + response.statusCode() + ": " + response.body());
            }
        }

        String responseBody = response.body();
        logger.info("Raw OpenAI response: {}", responseBody);
        
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
        Map<String, Object> firstChoice = choices.get(0);
        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
        
        return (String) message.get("content");
    }
    
    private String buildEnhancedPrompt(String prompt, Map<String, Object> context) {
        StringBuilder enhancedPrompt = new StringBuilder();
        enhancedPrompt.append("You are a financial advisor AI assistant. ");
        enhancedPrompt.append("Provide clear, actionable financial advice based on the following information:\n\n");
        
        if (context != null && !context.isEmpty()) {
            enhancedPrompt.append("Context:\n");
            context.forEach((key, value) -> enhancedPrompt.append("- ").append(key).append(": ").append(value).append("\n"));
            enhancedPrompt.append("\n");
        }
        
        enhancedPrompt.append("Request: ").append(prompt).append("\n\n");
        enhancedPrompt.append("Please provide a detailed, professional response with specific recommendations.");
        
        return enhancedPrompt.toString();
    }
    
    private String buildRecommendationPrompt(String userContext) {
        return String.format(
                "Based on the following user financial context, generate 3-5 specific, actionable recommendations:\n\n%s\n\n" +
                "Provide recommendations in the following format:\n" +
                "1. [Recommendation Title] - [Brief description]\n" +
                "2. [Recommendation Title] - [Brief description]\n" +
                "etc.\n\n" +
                "Focus on practical, achievable advice that can help improve financial health.",
                userContext
        );
    }
    
    private String buildSpendingAnalysisPrompt(List<Map<String, Object>> transactions) {
        String transactionsJson = transactions.stream()
                .map(t -> String.format("{\"description\":\"%s\",\"amount\":%.2f,\"category\":\"%s\"}",
                        t.get("description"), t.get("amount"), t.get("category")))
                .collect(Collectors.joining(","));

        return "Analyze the following user transactions and provide a spending pattern analysis. " +
                "Transactions: [" + transactionsJson + "]. " +
                "Return a JSON object with the following fields: 'totalSpending' (double), 'topCategories' (list of strings), " +
                "'insights' (string), and 'suggestions' (list of strings). " +
                "ONLY return the raw JSON object, without any additional text, explanations, or markdown formatting.";
    }
    
    private String buildCategorizationPrompt(String description, List<Category> categories) {
        List<String> categoryNames = categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("Categorize the following transaction description into one of the available categories:\n\n");
        prompt.append("Transaction: ").append(description).append("\n\n");
        prompt.append("Available categories:\n");
        
        categoryNames.forEach(name -> 
                prompt.append("- ").append(name).append("\n"));
        
        prompt.append("\nRespond with only the category name, nothing else.");
        
        return prompt.toString();
    }
    
    private String buildBudgetPrompt(Double userIncome, Map<String, Double> currentSpending, List<String> goals) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a budget recommendation based on the following information:\n\n");
        prompt.append("Monthly Income: ").append(userIncome).append("\n\n");
        prompt.append("Current Spending:\n");
        currentSpending.forEach((category, amount) -> 
                prompt.append("- ").append(category).append(": ").append(amount).append("\n"));
        prompt.append("\nFinancial Goals: ").append(goals).append("\n\n");
        prompt.append("Provide a detailed budget recommendation with specific allocations.");
        
        return prompt.toString();
    }
    
    private String buildAnomalyDetectionPrompt(List<Transaction> transactions, Map<String, Object> historicalPatterns) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Detect anomalies in the following recent transactions compared to historical patterns:\n\n");
        prompt.append("Recent Transactions:\n");
        transactions.forEach(t -> 
                prompt.append("- ").append(t.getDescription()).append(": ").append(t.getAmount()).append("\n"));
        prompt.append("\nHistorical Patterns: ").append(historicalPatterns).append("\n\n");
        prompt.append("Provide analysis in JSON format with anomaly detection results.");
        
        return prompt.toString();
    }
    
    private String buildSavingsPrompt(Map<String, Double> spendingData, Map<String, Object> userProfile) {
        return String.format("""
            Analyze the following spending data and user profile to identify savings opportunities:
            
            Spending Data: %s
            User Profile: %s
            
            Provide specific, actionable savings recommendations with estimated monthly savings amounts.
            Focus on realistic changes that can be implemented immediately.
            """, spendingData, userProfile);
    }
    
    String buildSpendingInsightsPrompt(Map<String, Object> userData) {
        return String.format("""
            Analyze the following user financial data and provide comprehensive spending insights:
            
            User Data: %s
            
            Provide insights on:
            1. Overall spending patterns and trends
            2. Category-wise analysis
            3. Budget performance
            4. Goal progress
            5. Areas of concern
            6. Positive financial behaviors
            
            Format the response in a clear, actionable manner with specific recommendations.
            """, userData);
    }
    
    String buildCategoryAnalysisPrompt(Map<String, Object> categoryBreakdown, Map<String, Object> userProfile) {
        return String.format("""
            Analyze the following category spending breakdown for the user:
            
            Category Breakdown: %s
            User Profile: %s
            
            Provide insights on:
            1. Top spending categories and their implications
            2. Category-wise recommendations
            3. Comparison with recommended spending ratios
            4. Potential areas for optimization
            5. Category-specific savings opportunities
            
            Be specific and actionable in your recommendations.
            """, categoryBreakdown, userProfile);
    }
    
    private String buildBudgetAnalysisPrompt(Map<String, Object> budgetData, Map<String, Object> actualSpending) {
        return String.format("""
            Compare the following budget data with actual spending:
            
            Budget Data: %s
            Actual Spending: %s
            
            Provide analysis on:
            1. Budget performance (over/under budget)
            2. Category-wise budget adherence
            3. Reasons for budget variances
            4. Recommendations for budget adjustments
            5. Strategies to stay within budget
            
            Be constructive and provide actionable advice.
            """, budgetData, actualSpending);
    }
    
    private String buildGoalAnalysisPrompt(List<Map<String, Object>> goals, Map<String, Object> spendingData) {
        return String.format("""
            Analyze the following financial goals and current spending patterns:
            
            Goals: %s
            Spending Data: %s
            
            Provide analysis on:
            1. Goal progress and timeline
            2. Impact of current spending on goal achievement
            3. Recommendations to accelerate goal progress
            4. Potential obstacles and solutions
            5. Goal prioritization suggestions
            
            Focus on practical steps to achieve financial goals.
            """, goals, spendingData);
    }
    
    private String buildTrendAnalysisPrompt(Map<String, Object> spendingTrends, Map<String, Object> userProfile) {
        return String.format("""
            Analyze the following spending trends:
            
            Spending Trends: %s
            User Profile: %s
            
            Provide analysis on:
            1. Overall spending trends (increasing/decreasing)
            2. Category-wise trend analysis
            3. Seasonal patterns if any
            4. Predictions for future spending
            5. Recommendations based on trends
            
            Identify both positive and concerning trends with actionable insights.
            """, spendingTrends, userProfile);
    }
    
    private String buildPersonalizedRecommendationsPrompt(Map<String, Object> userData, String analysisType) {
        return String.format("""
            Generate personalized financial recommendations based on the following data:
            
            User Data: %s
            Analysis Type: %s
            
            Provide recommendations that are:
            1. Specific to the user's financial situation
            2. Actionable and implementable
            3. Prioritized by impact and effort
            4. Tailored to the analysis type
            5. Realistic and achievable
            
            Format as a numbered list with clear action items.
            """, userData, analysisType);
    }
    
    private String buildSpendingAlertsPrompt(Map<String, Object> userData, Map<String, Object> thresholds) {
        return String.format("""
            Analyze the following user data against spending thresholds:
            
            User Data: %s
            Thresholds: %s
            
            Generate spending alerts for:
            1. Budget overruns
            2. Unusual spending patterns
            3. Category spending limits
            4. Goal achievement risks
            5. Savings rate issues
            
            Format alerts with urgency levels and specific recommendations.
            """, userData, thresholds);
    }
    
    private String buildMonthlySummaryPrompt(Map<String, Object> monthlyData, Map<String, Object> userProfile) {
        return String.format("""
            Create a comprehensive monthly financial summary:
            
            Monthly Data: %s
            User Profile: %s
            
            Include:
            1. Total spending and income
            2. Budget performance
            3. Top spending categories
            4. Goal progress
            5. Key achievements
            6. Areas for improvement
            7. Next month's focus areas
            
            Make it engaging and motivational while being informative.
            """, monthlyData, userProfile);
    }
    
    private String buildConversationalAdvicePrompt(Map<String, Object> userData, String userQuestion) {
        return String.format("""
            Provide conversational financial advice based on the user's question and financial data:
            
            User Data: %s
            User Question: %s
            
            Provide advice that is:
            1. Directly relevant to the question
            2. Based on the user's financial situation
            3. Conversational and friendly
            4. Actionable and specific
            5. Educational and informative
            
            Keep the tone helpful and supportive.
            """, userData, userQuestion);
    }
    
    private String buildOptimizationSuggestionsPrompt(Map<String, Object> userData, String optimizationGoal) {
        return String.format("""
            Generate optimization suggestions for the following goal:
            
            User Data: %s
            Optimization Goal: %s
            
            Provide suggestions that:
            1. Align with the specific optimization goal
            2. Are based on the user's current spending patterns
            3. Include estimated savings/benefits
            4. Are realistic and implementable
            5. Include implementation steps
            
            Prioritize suggestions by impact and ease of implementation.
            """, userData, optimizationGoal);
    }
    
    private String buildFinancialHealthScorePrompt(Map<String, Object> userData) {
        return String.format("""
            Calculate a comprehensive financial health score based on the following data:
            
            User Data: %s
            
            Provide a JSON response with:
            1. Overall score (0-100)
            2. Grade (A, B, C, D, F)
            3. Detailed analysis
            4. Strengths list
            5. Weaknesses list
            6. Specific recommendations
            
            Consider factors like savings rate, debt levels, budget adherence, emergency fund, and spending patterns.
            """, userData);
    }
    
    private String buildComparisonAnalysisPrompt(Map<String, Object> currentData, Map<String, Object> previousData, String comparisonType) {
        return String.format("""
            Perform a %s comparison analysis:
            
            Current Period Data: %s
            Previous Period Data: %s
            
            Provide analysis on:
            1. Overall spending changes
            2. Category-wise comparisons
            3. Budget performance changes
            4. Goal progress comparison
            5. Trend identification
            6. Recommendations based on changes
            
            Highlight both improvements and areas needing attention.
            """, comparisonType, currentData, previousData);
    }
    
    private String buildActionableStepsPrompt(Map<String, Object> userData, String priority) {
        return String.format("""
            Generate actionable financial steps with %s priority:
            
            User Data: %s
            
            Provide steps that are:
            1. Prioritized by urgency and impact
            2. Specific and measurable
            3. Time-bound with deadlines
            4. Realistic and achievable
            5. Aligned with the user's financial situation
            
            Include estimated time commitment and expected outcomes for each step.
            """, priority, userData);
    }
    
    private Map<String, Object> parseFinancialHealthScore(String response) {
        try {
            // Try to parse as JSON first
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            // Fallback to simple parsing
            logger.warn("Could not parse financial health score as JSON, using fallback parsing");
            return Map.of(
                "score", 75,
                "grade", "B",
                "analysis", response,
                "strengths", Arrays.asList("Good income", "Some savings"),
                "weaknesses", Arrays.asList("High expenses", "Limited investments"),
                "recommendations", Arrays.asList("Reduce expenses", "Increase savings")
            );
        }
    }
    
    private List<String> parseRecommendations(String response) {
        // Simple parsing assuming recommendations are separated by newlines
        return Arrays.asList(response.split("\\r?\\n"));
    }
    
    private Map<String, Object> parseAnalysisResponse(String response) {
        try {
            // Find the start and end of the JSON object
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}");

            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                String jsonResponse = response.substring(jsonStart, jsonEnd + 1);
                return objectMapper.readValue(jsonResponse, Map.class);
            } else {
                logger.warn("No valid JSON object found in OpenAI response: {}", response);
                return Map.of("error", "Could not parse analysis from AI response.");
            }
        } catch (IOException e) {
            logger.error("Failed to parse JSON response from OpenAI: {}", response, e);
            return Map.of("error", "Failed to deserialize AI response.");
        }
    }
} 