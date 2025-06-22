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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "ai.llm.provider", havingValue = "anthropic")
public class AnthropicLLMService implements LLMService {

    private static final Logger logger = LoggerFactory.getLogger(AnthropicLLMService.class);
    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String ANTHROPIC_VERSION = "2023-06-01";

    @Value("${ai.llm.anthropic.api-key:}")
    private String apiKey;

    @Value("${ai.llm.anthropic.model:claude-3-haiku-20240307}")
    private String model;

    @Value("${ai.llm.anthropic.max-tokens:1000}")
    private int maxTokens;

    @Value("${ai.llm.anthropic.temperature:0.7}")
    private double temperature;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AnthropicLLMService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.trim().isEmpty();
    }

    @Override
    public String getProvider() {
        return "anthropic";
    }

    @Override
    public String getModel() {
        return model;
    }

    private String callAnthropic(String prompt) throws IOException, InterruptedException {
        if (!isAvailable()) {
            throw new IllegalStateException("Anthropic API key not configured");
        }

        logger.info("Sending prompt to Anthropic (length: {} chars): {}", prompt.length(), prompt);

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "max_tokens", maxTokens,
                "temperature", temperature
        );

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        logger.info("Anthropic request body: {}", requestBodyJson);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ANTHROPIC_API_URL))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", ANTHROPIC_VERSION)
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.error("Anthropic API error: {} - {}", response.statusCode(), response.body());
            throw new RuntimeException("Anthropic API call failed with status " + response.statusCode() + ": " + response.body());
        }

        logger.info("Anthropic response received: {}", response.body());
        Map<String, Object> responseMap = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> content = (List<Map<String, Object>>) responseMap.get("content");
        return (String) content.get(0).get("text");
    }

    //================================================================================
    // MOCK IMPLEMENTATIONS FOR NOW - Should be replaced with actual prompt engineering
    //================================================================================

    @Override
    public Map<String, Object> analyzeSpendingPatterns(List<Map<String, Object>> transactions) {
        try {
            if (!isAvailable()) {
                logger.warn("Anthropic API key not configured, returning mock spending analysis");
                return Map.of("error", "Anthropic API key not configured");
            }
            String prompt = buildSpendingAnalysisPrompt(transactions);
            String response = callAnthropic(prompt);
            return parseAnalysisResponse(response);
        } catch (Exception e) {
            logger.error("Error analyzing spending patterns with Anthropic: {}", e.getMessage(), e);
            return Map.of("error", "Unable to analyze spending patterns with Anthropic");
        }
    }

    private String buildSpendingAnalysisPrompt(List<Map<String, Object>> transactions) {
        // This can be a very sophisticated prompt. For now, a simple one.
        String transactionsJson = transactions.stream()
            .map(t -> String.format("{\"description\": \"%s\", \"amount\": %.2f, \"category\": \"%s\"}",
                t.get("description"), t.get("amount"), t.get("category")))
            .collect(Collectors.joining(",\n"));

        return "Analyze the following JSON array of transactions and provide a summary of spending patterns. " +
               "Identify top spending categories, potential savings, and any notable trends. " +
               "Return the analysis as a JSON object with keys: 'totalSpending', 'topCategories' (a list of strings), " +
               "'insights' (a string), and 'suggestions' (a list of strings).\n\n" +
               "Transactions:\n[" + transactionsJson + "]";
    }
    
    private Map<String, Object> parseAnalysisResponse(String response) {
        try {
            // Find the start and end of the JSON object
            int jsonStart = response.indexOf('{');
            int jsonEnd = response.lastIndexOf('}');

            if (jsonStart != -1 && jsonEnd != -1) {
                String jsonString = response.substring(jsonStart, jsonEnd + 1);
                return objectMapper.readValue(jsonString, Map.class);
            } else {
                logger.error("No JSON object found in the LLM response: {}", response);
                return Map.of("error", "No JSON object found in response", "rawResponse", response);
            }
        } catch (IOException e) {
            logger.error("Failed to parse JSON response from LLM: {}", response, e);
            return Map.of("error", "Failed to parse analysis response", "rawResponse", response);
        }
    }
    
    // NOTE: Other methods from LLMService are not implemented yet
    // They would follow a similar pattern: build a prompt, call the API, parse the response.
    // For now, they will throw UnsupportedOperationException.

    @Override
    public String generateAnalysis(String prompt, Map<String, Object> context) {
        throw new UnsupportedOperationException("generateAnalysis not implemented for Anthropic yet.");
    }

    @Override
    public List<String> generateRecommendations(String userContext) {
        throw new UnsupportedOperationException("generateRecommendations not implemented for Anthropic yet.");
    }

    @Override
    public String categorizeTransaction(String description, List<Category> categories) {
        throw new UnsupportedOperationException("categorizeTransaction not implemented for Anthropic yet.");
    }

    @Override
    public String generateBudgetRecommendation(Double userIncome, Map<String, Double> currentSpending, List<String> goals) {
        throw new UnsupportedOperationException("generateBudgetRecommendation not implemented for Anthropic yet.");
    }

    @Override
    public Map<String, Object> detectAnomalies(List<Transaction> transactions, Map<String, Object> historicalPatterns) {
        throw new UnsupportedOperationException("detectAnomalies not implemented for Anthropic yet.");
    }

    @Override
    public String generateSavingsOpportunities(Map<String, Double> spendingData, Map<String, Object> userProfile) {
        throw new UnsupportedOperationException("generateSavingsOpportunities not implemented for Anthropic yet.");
    }
} 