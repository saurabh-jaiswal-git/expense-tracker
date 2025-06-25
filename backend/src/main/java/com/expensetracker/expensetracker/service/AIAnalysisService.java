package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.AIAnalysis;
import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Recommendation;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.AIAnalysisRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI Analysis Service for generating and managing AI-powered financial insights
 * Coordinates between LLM service and database operations
 */
@Service
public class AIAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisService.class);
    
    private final LLMService llmService;
    private final AIAnalysisRepository aiAnalysisRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    public AIAnalysisService(LLMService llmService, AIAnalysisRepository aiAnalysisRepository, UserRepository userRepository) {
        this.llmService = llmService;
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Analyzes a user's spending patterns based on their transactions.
     * @param userId The ID of the user to analyze.
     * @param transactions The list of transactions to analyze.
     * @return An AIAnalysis object with the spending analysis.
     */
    public AIAnalysis analyzeUserSpending(Long userId, List<Map<String, Object>> transactions) {
        long startTime = System.currentTimeMillis();
        logger.info("Making real OpenAI API call for user {} with {} transactions", userId, transactions.size());

        if (!llmService.isAvailable()) {
            logger.warn("LLM service is not available (no API key configured). Using mock data.");
            return createMockAnalysis(userId, "SPENDING_PATTERN", "Mock spending analysis due to missing API key.");
        }

        try {
            Map<String, Object> analysisData = llmService.analyzeSpendingPatterns(transactions);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            AIAnalysis analysis = AIAnalysis.builder()
                    .user(user)
                    .analysisType("SPENDING_PATTERN")
                    .analysisData(convertMapToJson(analysisData))
                    .insights("Analysis completed successfully.")
                    .confidenceScore(0.85)
                    .modelUsed(llmService.getModel())
                    .processingTimeMs(processingTime)
                    .createdAt(LocalDateTime.now())
                    .build();

            logger.info("Generated real spending analysis for user {} in {}ms using {}", userId, processingTime, llmService.getProvider());
            return aiAnalysisRepository.save(analysis);
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("quota exceeded")) {
                logger.warn("OpenAI API quota exceeded for user {}. Falling back to mock data.", userId);
                return createMockAnalysis(userId, "SPENDING_PATTERN", 
                    "Analysis unavailable due to OpenAI API quota exceeded. Please check your billing and plan details. Using mock data as fallback.");
            } else if (e.getMessage().contains("authentication failed") || e.getMessage().contains("access forbidden")) {
                logger.warn("OpenAI API authentication failed for user {}. Falling back to mock data.", userId);
                return createMockAnalysis(userId, "SPENDING_PATTERN", 
                    "Analysis unavailable due to OpenAI API authentication issues. Please check your API key. Using mock data as fallback.");
            } else {
                logger.error("Error analyzing spending patterns for user {}: {}", userId, e.getMessage(), e);
                return createMockAnalysis(userId, "SPENDING_PATTERN", 
                    "Analysis failed due to technical issues. Using mock data as fallback.");
            }
        }
    }
    
    /**
     * Generate personalized recommendations for a user
     * @param userId User ID to generate recommendations for
     * @return List of recommendations
     */
    public List<Recommendation> generateRecommendations(Long userId) {
        try {
            // Check if LLM service is available
            if (!llmService.isAvailable()) {
                logger.warn("LLM service is not available (no API key configured). Using mock data.");
                return createMockRecommendations(userId);
            }
            
            // TODO: Get user and transactions from repository
            // User user = userRepository.findById(userId)
            //     .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            // List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            
            // For now, create mock data
            User user = createMockUser(userId);
            List<Transaction> transactions = createMockTransactions();
            
            // Build user context
            String userContext = buildUserContext(user, transactions);
            
            logger.info("Making real OpenAI API call for recommendations for user {}", userId);
            
            // Generate recommendations using LLM
            List<String> recommendationTexts = llmService.generateRecommendations(userContext);
            
            // Convert to Recommendation entities
            List<Recommendation> recommendations = new ArrayList<>();
            for (String recommendationText : recommendationTexts) {
                Recommendation recommendation = new Recommendation();
                recommendation.setUser(user);
                recommendation.setRecommendationType(Recommendation.RecommendationType.SPENDING_REDUCTION);
                recommendation.setTitle(extractTitle(recommendationText));
                recommendation.setDescription(recommendationText);
                recommendation.setPriority(Recommendation.Priority.MEDIUM);
                recommendation.setConfidenceScore(0.80);
                recommendation.setModelUsed(llmService.getModel());
                recommendation.setCreatedAt(LocalDateTime.now());
                
                recommendations.add(recommendation);
            }
            
            // TODO: Save to database
            // recommendationRepository.saveAll(recommendations);
            
            logger.info("Generated {} real recommendations for user {} using {}", 
                       recommendations.size(), userId, llmService.getProvider());
            
            return recommendations;
            
        } catch (Exception e) {
            logger.error("Error generating recommendations for user {}: {}", userId, e.getMessage(), e);
            // Return mock data as fallback
            return createMockRecommendations(userId);
        }
    }
    
    /**
     * Categorize a transaction using AI
     * @param description Transaction description
     * @return Suggested category name
     */
    public String categorizeTransaction(String description) {
        try {
            // Check if LLM service is available
            if (!llmService.isAvailable()) {
                logger.warn("LLM service is not available (no API key configured). Using mock categorization.");
                return "Food"; // Mock category
            }
            
            // TODO: Get categories from repository
            // List<Category> categories = categoryRepository.findAll();
            
            // For now, create mock categories
            List<Category> categories = createMockCategories();
            
            logger.info("Making real OpenAI API call for transaction categorization: '{}'", description);
            
            String category = llmService.categorizeTransaction(description, categories);
            
            logger.info("Categorized '{}' as '{}' using {}", description, category, llmService.getProvider());
            
            return category;
            
        } catch (Exception e) {
            logger.error("Error categorizing transaction '{}': {}", description, e.getMessage(), e);
            return "Uncategorized";
        }
    }
    
    /**
     * Generate budget recommendations
     * @param userId User ID
     * @return Budget recommendation text
     */
    public String generateBudgetRecommendation(Long userId) {
        try {
            // Check if LLM service is available
            if (!llmService.isAvailable()) {
                logger.warn("LLM service is not available (no API key configured). Using mock budget recommendation.");
                return "Based on your income of 50,000 INR, consider allocating 50% to needs, 30% to wants, and 20% to savings.";
            }
            
            // TODO: Get user data from repository
            // User user = userRepository.findById(userId)
            //     .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // For now, use mock data
            Double userIncome = 50000.0;
            Map<String, Double> currentSpending = Map.of(
                "Food", 8000.0,
                "Transportation", 3000.0,
                "Entertainment", 2000.0,
                "Utilities", 1500.0
            );
            List<String> goals = Arrays.asList("Save for vacation", "Emergency fund");
            
            logger.info("Making real OpenAI API call for budget recommendation for user {}", userId);
            
            String recommendation = llmService.generateBudgetRecommendation(userIncome, currentSpending, goals);
            
            logger.info("Generated real budget recommendation for user {} using {}", userId, llmService.getProvider());
            
            return recommendation;
            
        } catch (Exception e) {
            logger.error("Error generating budget recommendation for user {}: {}", userId, e.getMessage(), e);
            return "Unable to generate budget recommendation at this time.";
        }
    }
    
    /**
     * Detect spending anomalies
     * @param userId User ID
     * @return Anomaly detection results
     */
    public Map<String, Object> detectAnomalies(Long userId) {
        try {
            // TODO: Get recent transactions and historical patterns
            // List<Transaction> recentTransactions = transactionRepository.findRecentTransactions(userId, LocalDate.now().minusDays(7));
            // Map<String, Object> historicalPatterns = getHistoricalPatterns(userId);
            
            // For now, use mock data
            List<Transaction> recentTransactions = createMockTransactions();
            Map<String, Object> historicalPatterns = Map.of("average_daily_spending", 500.0);
            
            return llmService.detectAnomalies(recentTransactions, historicalPatterns);
            
        } catch (Exception e) {
            logger.error("Error detecting anomalies for user {}: {}", userId, e.getMessage(), e);
            return Map.of("error", "Unable to detect anomalies");
        }
    }
    
    /**
     * Get the LLM service instance for testing purposes
     * @return LLM service instance
     */
    public LLMService getLlmService() {
        return llmService;
    }
    
    // Helper methods
    private String buildUserContext(User user, List<Transaction> transactions) {
        StringBuilder context = new StringBuilder();
        context.append("User Profile:\n");
        context.append("- Monthly Income: ").append(user.getMonthlyIncome()).append("\n");
        context.append("- Currency: ").append(user.getCurrency()).append("\n");
        context.append("- Timezone: ").append(user.getTimezone()).append("\n\n");
        
        context.append("Recent Spending Summary:\n");
        Map<String, Double> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategory() != null ? t.getCategory().getName() : "Uncategorized",
                        Collectors.summingDouble(t -> t.getAmount().doubleValue())
                ));
        
        categoryTotals.forEach((category, total) -> 
                context.append("- ").append(category).append(": ").append(total).append("\n"));
        
        return context.toString();
    }
    
    private String extractInsights(Map<String, Object> analysisResults) {
        if (analysisResults.containsKey("insights")) {
            return (String) analysisResults.get("insights");
        }
        return "Analysis completed successfully.";
    }
    
    private String extractTitle(String recommendationText) {
        // Extract first part before dash or colon as title
        String[] parts = recommendationText.split("[-:]", 2);
        return parts[0].trim();
    }
    
    private String convertToJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.warn("Failed to convert object to JSON: {}", e.getMessage());
            return obj.toString();
        }
    }
    
    private String convertMapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            logger.warn("Failed to convert map to JSON: {}", e.getMessage());
            return map.toString();
        }
    }
    
    // Mock data methods (to be removed when repositories are implemented)
    private User createMockUser(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setEmail("user" + userId + "@example.com");
        user.setFirstName("User");
        user.setLastName("" + userId);
        user.setMonthlyIncome(new BigDecimal("50000.00"));
        user.setCurrency("INR");
        user.setTimezone("Asia/Kolkata");
        return user;
    }
    
    private List<Transaction> createMockTransactions() {
        // Create mock transactions for testing
        List<Transaction> transactions = new ArrayList<>();
        
        // Add some sample transactions
        Transaction t1 = Transaction.builder()
                .amount(new java.math.BigDecimal("1500.00"))
                .description("Lunch at restaurant")
                .transactionType(Transaction.TransactionType.EXPENSE)
                .transactionDate(java.time.LocalDate.now())
                .build();
        
        Transaction t2 = Transaction.builder()
                .amount(new java.math.BigDecimal("500.00"))
                .description("Uber ride")
                .transactionType(Transaction.TransactionType.EXPENSE)
                .transactionDate(java.time.LocalDate.now())
                .build();
        
        transactions.add(t1);
        transactions.add(t2);
        
        return transactions;
    }
    
    private List<Category> createMockCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(Category.builder().name("Food").description("Food and dining expenses").icon("🍕").color("#FF6B6B").build());
        categories.add(Category.builder().name("Transportation").description("Transport and travel").icon("🚗").color("#4ECDC4").build());
        categories.add(Category.builder().name("Entertainment").description("Entertainment and leisure").icon("🎬").color("#45B7D1").build());
        categories.add(Category.builder().name("Shopping").description("Shopping and retail").icon("🛍️").color("#96CEB4").build());
        categories.add(Category.builder().name("Utilities").description("Bills and utilities").icon("💡").color("#FFEAA7").build());
        categories.add(Category.builder().name("Healthcare").description("Health and medical").icon("🏥").color("#DDA0DD").build());
        categories.add(Category.builder().name("Education").description("Education and learning").icon("📚").color("#98D8C8").build());
        categories.add(Category.builder().name("Travel").description("Travel and vacations").icon("✈️").color("#F7DC6F").build());
        categories.add(Category.builder().name("Other").description("Miscellaneous expenses").icon("📦").color("#BB8FCE").build());
        return categories;
    }
    
    public AIAnalysis createMockAnalysis(Long userId, String analysisType, String insights) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        AIAnalysis analysis = AIAnalysis.builder()
                .user(user)
                .analysisType(analysisType)
                .analysisData("{\"totalSpent\": 15000, \"topCategory\": \"Food\", \"savingsOpportunity\": 2000}")
                .insights(insights)
                .confidenceScore(0.75)
                .modelUsed("mock-model")
                .processingTimeMs(0L)
                .createdAt(LocalDateTime.now())
                .build();
        return analysis;
    }
    
    private List<Recommendation> createMockRecommendations(Long userId) {
        User user = createMockUser(userId);
        List<Recommendation> recommendations = new ArrayList<>();
        
        Recommendation rec1 = new Recommendation();
        rec1.setUser(user);
        rec1.setRecommendationType(Recommendation.RecommendationType.SPENDING_REDUCTION);
        rec1.setTitle("Reduce Dining Out Expenses");
        rec1.setDescription("You spend 8,000 INR monthly on dining out. Consider cooking at home 3-4 times per week to save 3,000 INR monthly.");
        rec1.setPriority(Recommendation.Priority.HIGH);
        rec1.setConfidenceScore(0.85);
        rec1.setModelUsed("mock-model");
        rec1.setCreatedAt(LocalDateTime.now());
        recommendations.add(rec1);
        
        Recommendation rec2 = new Recommendation();
        rec2.setUser(user);
        rec2.setRecommendationType(Recommendation.RecommendationType.BUDGET_OPTIMIZATION);
        rec2.setTitle("Create Emergency Fund");
        rec2.setDescription("Start building an emergency fund with 10% of your monthly income (5,000 INR) to cover 3-6 months of expenses.");
        rec2.setPriority(Recommendation.Priority.MEDIUM);
        rec2.setConfidenceScore(0.80);
        rec2.setModelUsed("mock-model");
        rec2.setCreatedAt(LocalDateTime.now());
        recommendations.add(rec2);
        
        return recommendations;
    }
} 