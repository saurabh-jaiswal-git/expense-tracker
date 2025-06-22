package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.AIAnalysis;
import com.expensetracker.expensetracker.entity.Recommendation;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.service.AIAnalysisService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI-powered financial analysis endpoints
 * Provides endpoints for spending analysis, recommendations, and AI insights
 */
@RestController
@RequestMapping("/api/ai")
@Validated
public class AIAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIAnalysisController.class);
    
    @Autowired
    private AIAnalysisService aiAnalysisService;
    
    /**
     * Get AI analysis for user spending patterns
     * @param userId User ID to analyze
     * @return AI Analysis result
     */
    @GetMapping("/analysis/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<AIAnalysis> getAnalysis(@PathVariable @Positive Long userId) {
        try {
            logger.info("Generating AI analysis for user: {}", userId);
            // For now, return mock data since we need transactions for real analysis
            AIAnalysis analysis = aiAnalysisService.createMockAnalysis(userId, "SPENDING_PATTERN", "Mock analysis for user " + userId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            logger.error("Error generating analysis for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Analyze spending data with custom parameters
     * @param request Analysis request containing userId, analysisType, and data
     * @return AI Analysis result
     */
    @PostMapping("/analyze")
    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.principal.id")
    public ResponseEntity<AIAnalysis> analyzeSpending(@Valid @RequestBody AIAnalysisRequest request) {
        if (request == null || request.getUserId() == null || request.getTransactions() == null) {
            return ResponseEntity.badRequest().build();
        }
        AIAnalysis analysis = aiAnalysisService.analyzeUserSpending(request.getUserId(), request.getTransactions());
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * Get AI-generated recommendations for a user
     * @param userId User ID to generate recommendations for
     * @return List of recommendations
     */
    @GetMapping("/recommendations/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable @Positive Long userId) {
        try {
            logger.info("Generating AI recommendations for user: {}", userId);
            List<Recommendation> recommendations = aiAnalysisService.generateRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            logger.error("Error generating recommendations for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Categorize a transaction using AI
     * @param request Transaction description
     * @return Suggested category name
     */
    @PostMapping("/categorize")
    public ResponseEntity<Map<String, String>> categorizeTransaction(@Valid @RequestBody CategorizationRequest request) {
        try {
            logger.info("Categorizing transaction: {}", request.getDescription());
            String category = aiAnalysisService.categorizeTransaction(request.getDescription());
            return ResponseEntity.ok(Map.of("category", category));
        } catch (Exception e) {
            logger.error("Error categorizing transaction: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to categorize transaction"));
        }
    }
    
    /**
     * Generate budget recommendations
     * @param userId User ID
     * @return Budget recommendation text
     */
    @GetMapping("/budget/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, String>> getBudgetRecommendation(@PathVariable @Positive Long userId) {
        try {
            logger.info("Generating budget recommendation for user: {}", userId);
            String recommendation = aiAnalysisService.generateBudgetRecommendation(userId);
            return ResponseEntity.ok(Map.of("recommendation", recommendation));
        } catch (Exception e) {
            logger.error("Error generating budget recommendation for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to generate budget recommendation"));
        }
    }
    
    /**
     * Detect spending anomalies
     * @param userId User ID
     * @return Anomaly detection results
     */
    @GetMapping("/anomalies/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> detectAnomalies(@PathVariable @Positive Long userId) {
        try {
            logger.info("Detecting anomalies for user: {}", userId);
            Map<String, Object> anomalies = aiAnalysisService.detectAnomalies(userId);
            return ResponseEntity.ok(anomalies);
        } catch (Exception e) {
            logger.error("Error detecting anomalies for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to detect anomalies"));
        }
    }
    
    /**
     * Test AI integration without database persistence
     * @param request Analysis request containing userId, analysisType, and data
     * @return AI Analysis result (not saved to database)
     */
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN') or #request.userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> testAI(@Valid @RequestBody AIAnalysisRequest request) {
        try {
            logger.info("Testing AI integration for user {} with {} transactions", request.getUserId(), request.getTransactions().size());
            
            if (!aiAnalysisService.getLlmService().isAvailable()) {
                return ResponseEntity.ok(Map.of(
                    "status", "mock",
                    "message", "LLM service not available (no API key configured)",
                    "analysis", Map.of("totalSpending", 15000, "topCategories", List.of("Food"), "insights", "Mock analysis")
                ));
            }
            
            // Test the AI call directly
            Map<String, Object> analysisData = aiAnalysisService.getLlmService().analyzeSpendingPatterns(request.getTransactions());
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "AI integration test successful",
                "analysis", analysisData,
                "model", aiAnalysisService.getLlmService().getModel(),
                "provider", aiAnalysisService.getLlmService().getProvider()
            ));
            
        } catch (RuntimeException e) {
            if (e.getMessage().contains("quota exceeded")) {
                return ResponseEntity.status(429).body(Map.of(
                    "status", "quota_exceeded",
                    "message", "OpenAI API quota exceeded. Please check your billing and plan details.",
                    "error", e.getMessage()
                ));
            } else if (e.getMessage().contains("authentication failed") || e.getMessage().contains("access forbidden")) {
                return ResponseEntity.status(401).body(Map.of(
                    "status", "auth_failed",
                    "message", "OpenAI API authentication failed. Please check your API key.",
                    "error", e.getMessage()
                ));
            } else {
                return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "AI integration test failed",
                    "error", e.getMessage()
                ));
            }
        } catch (Exception e) {
            logger.error("Unexpected error in AI test: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", "Unexpected error occurred",
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Health check for AI services
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            boolean llmAvailable = aiAnalysisService.getLlmService().isAvailable();
            return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "llm_available", llmAvailable,
                "llm_provider", aiAnalysisService.getLlmService().getProvider(),
                "llm_model", aiAnalysisService.getLlmService().getModel()
            ));
        } catch (Exception e) {
            logger.error("Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(503).body(Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Request DTO for AI analysis
     */
    public static class AIAnalysisRequest {
        @NotNull
        @Positive
        private Long userId;
        
        @NotNull
        @Size(min = 1, max = 1000)
        private List<Map<String, Object>> transactions;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public List<Map<String, Object>> getTransactions() {
            return transactions;
        }
        
        public void setTransactions(List<Map<String, Object>> transactions) {
            this.transactions = transactions;
        }
    }
    
    /**
     * Request DTO for transaction categorization
     */
    public static class CategorizationRequest {
        @NotNull
        @Size(min = 1, max = 500)
        private String description;
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
    }
} 