package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.entity.AIAnalysis;
import com.expensetracker.expensetracker.entity.Category;
import com.expensetracker.expensetracker.entity.Recommendation;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private LLMService llmService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // TODO: Add repository dependencies when they're created
    // @Autowired
    // private AIAnalysisRepository aiAnalysisRepository;
    // @Autowired
    // private RecommendationRepository recommendationRepository;
    // @Autowired
    // private TransactionRepository transactionRepository;
    // @Autowired
    // private UserRepository userRepository;
    
    /**
     * Analyze user spending patterns and generate insights
     * @param userId User ID to analyze
     * @return AI Analysis result
     */
    public AIAnalysis analyzeUserSpending(Long userId) {
        long startTime = System.currentTimeMillis();
        
        try {
            // TODO: Get user and transactions from repository
            // User user = userRepository.findById(userId)
            //     .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            // List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            
            // For now, create mock data
            User user = createMockUser(userId);
            List<Transaction> transactions = createMockTransactions();
            
            // Generate analysis using LLM
            Map<String, Object> analysisResults = llmService.analyzeSpendingPatterns(transactions);
            
            // Create AI Analysis entity
            AIAnalysis analysis = new AIAnalysis();
            analysis.setUser(user);
            analysis.setAnalysisType(AIAnalysis.AnalysisType.SPENDING_PATTERN);
            analysis.setAnalysisData(convertToJson(analysisResults));
            analysis.setInsights(extractInsights(analysisResults));
            analysis.setConfidenceScore(0.85); // Mock confidence score
            analysis.setModelUsed(llmService.getModel());
            analysis.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            analysis.setCreatedAt(LocalDateTime.now());
            
            // TODO: Save to database
            // aiAnalysisRepository.save(analysis);
            
            logger.info("Generated spending analysis for user {} in {}ms", userId, analysis.getProcessingTimeMs());
            
            return analysis;
            
        } catch (Exception e) {
            logger.error("Error analyzing user spending for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to analyze user spending", e);
        }
    }
    
    /**
     * Generate personalized recommendations for a user
     * @param userId User ID to generate recommendations for
     * @return List of recommendations
     */
    public List<Recommendation> generateRecommendations(Long userId) {
        try {
            // TODO: Get user and transactions from repository
            // User user = userRepository.findById(userId)
            //     .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            // List<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            
            // For now, create mock data
            User user = createMockUser(userId);
            List<Transaction> transactions = createMockTransactions();
            
            // Build user context
            String userContext = buildUserContext(user, transactions);
            
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
            
            logger.info("Generated {} recommendations for user {}", recommendations.size(), userId);
            
            return recommendations;
            
        } catch (Exception e) {
            logger.error("Error generating recommendations for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate recommendations", e);
        }
    }
    
    /**
     * Categorize a transaction using AI
     * @param description Transaction description
     * @return Suggested category name
     */
    public String categorizeTransaction(String description) {
        try {
            // TODO: Get categories from repository
            // List<Category> categories = categoryRepository.findAll();
            
            // For now, create mock categories
            List<Category> categories = createMockCategories();
            
            return llmService.categorizeTransaction(description, categories);
            
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
            
            return llmService.generateBudgetRecommendation(userIncome, currentSpending, goals);
            
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
        
        Category food = new Category();
        food.setId(1L);
        food.setName("Food & Dining");
        
        Category transport = new Category();
        transport.setId(2L);
        transport.setName("Transportation");
        
        Category entertainment = new Category();
        entertainment.setId(3L);
        entertainment.setName("Entertainment");
        
        categories.add(food);
        categories.add(transport);
        categories.add(entertainment);
        
        return categories;
    }
} 