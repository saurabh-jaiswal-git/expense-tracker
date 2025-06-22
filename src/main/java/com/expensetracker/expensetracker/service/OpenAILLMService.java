package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
public class OpenAILLMService implements LLMService {
    
    private static final Logger logger = LoggerFactory.getLogger(OpenAILLMService.class);
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    @Value("${ai.llm.api-key:}")
    private String apiKey;
    
    @Value("${ai.llm.model:gpt-4}")
    private String model;
    
    @Value("${ai.llm.max-tokens:1000}")
    private int maxTokens;
    
    @Value("${ai.llm.temperature:0.7}")
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
            String prompt = buildRecommendationPrompt(userContext);
            String response = callOpenAI(prompt);
            return parseRecommendations(response);
        } catch (Exception e) {
            logger.error("Error generating recommendations: {}", e.getMessage(), e);
            return Arrays.asList("Unable to generate recommendations at this time.");
        }
    }
    
    @Override
    public Map<String, Object> analyzeSpendingPatterns(List<Transaction> transactions) {
        try {
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
            throw new RuntimeException("OpenAI API call failed: " + response.statusCode());
        }
        
        Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
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
    
    private String buildSpendingAnalysisPrompt(List<Transaction> transactions) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze the following spending transactions and provide insights:\n\n");
        
        Map<String, Double> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));
        
        categoryTotals.forEach((category, total) -> 
                prompt.append("- ").append(category).append(": ").append(total).append("\n"));
        
        prompt.append("\nPlease provide analysis in JSON format with the following structure:\n");
        prompt.append("{\n");
        prompt.append("  \"totalSpending\": number,\n");
        prompt.append("  \"topCategories\": [\"category1\", \"category2\"],\n");
        prompt.append("  \"insights\": \"analysis text\",\n");
        prompt.append("  \"suggestions\": [\"suggestion1\", \"suggestion2\"]\n");
        prompt.append("}");
        
        return prompt.toString();
    }
    
    private String buildCategorizationPrompt(String description, List<Category> categories) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Categorize the following transaction description into one of the available categories:\n\n");
        prompt.append("Transaction: ").append(description).append("\n\n");
        prompt.append("Available categories:\n");
        
        categories.forEach(category -> 
                prompt.append("- ").append(category.getName()).append("\n"));
        
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
        StringBuilder prompt = new StringBuilder();
        prompt.append("Identify savings opportunities based on the following spending data:\n\n");
        prompt.append("Spending Breakdown:\n");
        spendingData.forEach((category, amount) -> 
                prompt.append("- ").append(category).append(": ").append(amount).append("\n"));
        prompt.append("\nUser Profile: ").append(userProfile).append("\n\n");
        prompt.append("Provide specific savings recommendations with estimated amounts.");
        
        return prompt.toString();
    }
    
    private List<String> parseRecommendations(String response) {
        return Arrays.stream(response.split("\n"))
                .filter(line -> line.matches("^\\d+\\..*"))
                .map(line -> line.replaceFirst("^\\d+\\.\\s*", ""))
                .collect(Collectors.toList());
    }
    
    private Map<String, Object> parseAnalysisResponse(String response) {
        try {
            return objectMapper.readValue(response, Map.class);
        } catch (Exception e) {
            logger.warn("Failed to parse JSON response, returning as text: {}", e.getMessage());
            return Map.of("analysis", response);
        }
    }
} 