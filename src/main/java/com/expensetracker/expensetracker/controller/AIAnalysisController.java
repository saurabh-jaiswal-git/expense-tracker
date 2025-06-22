package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.AIAnalysis;
import com.expensetracker.expensetracker.entity.Recommendation;
import com.expensetracker.expensetracker.service.AIAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for AI-powered financial analysis endpoints
 * Provides endpoints for spending analysis, recommendations, and AI insights
 */
@RestController
@RequestMapping("/api/ai")
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
    public ResponseEntity<AIAnalysis> getAnalysis(@PathVariable Long userId) {
        try {
            logger.info("Generating AI analysis for user: {}", userId);
            AIAnalysis analysis = aiAnalysisService.analyzeUserSpending(userId);
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
    public ResponseEntity<AIAnalysis> analyzeSpending(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String analysisType = (String) request.get("analysisType");
            Map<String, Object> data = (Map<String, Object>) request.get("data");
            
            logger.info("Analyzing spending for user: {}, type: {}", userId, analysisType);
            AIAnalysis analysis = aiAnalysisService.analyzeUserSpending(userId);
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            logger.error("Error analyzing spending: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get AI-generated recommendations for a user
     * @param userId User ID to generate recommendations for
     * @return List of recommendations
     */
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Recommendation>> getRecommendations(@PathVariable Long userId) {
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
    public ResponseEntity<Map<String, String>> categorizeTransaction(@RequestBody Map<String, String> request) {
        try {
            String description = request.get("description");
            if (description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Description is required"));
            }
            
            logger.info("Categorizing transaction: {}", description);
            String category = aiAnalysisService.categorizeTransaction(description);
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
    public ResponseEntity<Map<String, String>> getBudgetRecommendation(@PathVariable Long userId) {
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
    public ResponseEntity<Map<String, Object>> detectAnomalies(@PathVariable Long userId) {
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
     * Health check for AI services
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            Map<String, Object> status = Map.of(
                "status", "healthy",
                "service", "AI Analysis",
                "timestamp", System.currentTimeMillis()
            );
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            logger.error("AI service health check failed: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "unhealthy",
                "error", e.getMessage()
            ));
        }
    }
} 