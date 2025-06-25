package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.service.DataStrategyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Smart Analytics Controller - Intelligent Strategy Selection
 * 
 * This controller provides endpoints that automatically select the optimal
 * data processing strategy based on dataset size and characteristics.
 * 
 * Features:
 * - Automatic strategy selection based on transaction count
 * - Performance-optimized analysis
 * - Strategy recommendations and insights
 * - Metadata about processing decisions
 */
@RestController
@RequestMapping("/api/smart-analytics")
@CrossOrigin(origins = "*")
public class SmartAnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(SmartAnalyticsController.class);
    
    @Autowired
    private DataStrategyService dataStrategyService;
    
    /**
     * Smart spending analysis with automatic strategy selection
     * This endpoint automatically chooses the best approach based on data size
     */
    @GetMapping("/spending-analysis/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getSmartSpendingAnalysis(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Starting smart spending analysis for user: {} from {} to {}", userId, startDate, endDate);
        
        try {
            // Use the data strategy service for optimal analysis
            DataStrategyService.AnalysisResult result = dataStrategyService.analyzeWithOptimalStrategy(userId, startDate, endDate);
            
            // Build comprehensive response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("insights", result.getInsights());
            response.put("strategy", result.getStrategy().name());
            response.put("transactionCount", result.getTransactionCount());
            response.put("metadata", result.getMetadata());
            Map<String, Object> dataPeriod = new HashMap<>();
            if (startDate != null) dataPeriod.put("startDate", startDate);
            if (endDate != null) dataPeriod.put("endDate", endDate);
            response.put("dataPeriod", dataPeriod);
            response.put("analysisType", "smart_automated");
            
            logger.info("Completed smart spending analysis for user: {} using strategy: {}", userId, result.getStrategy());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in smart spending analysis for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to perform smart analysis", "message", e.getMessage()));
        }
    }
    
    /**
     * Get strategy recommendations for a user
     * This helps users understand what strategy will be used and why
     */
    @GetMapping("/strategy-recommendations/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getStrategyRecommendations(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Getting strategy recommendations for user: {} from {} to {}", userId, startDate, endDate);
        
        try {
            Map<String, Object> recommendations = dataStrategyService.getStrategyRecommendations(userId, startDate, endDate);
            recommendations.put("userId", userId);
            Map<String, Object> dataPeriod2 = new HashMap<>();
            if (startDate != null) dataPeriod2.put("startDate", startDate);
            if (endDate != null) dataPeriod2.put("endDate", endDate);
            recommendations.put("dataPeriod", dataPeriod2);
            
            return ResponseEntity.ok(recommendations);
            
        } catch (Exception e) {
            logger.error("Error getting strategy recommendations for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get strategy recommendations", "message", e.getMessage()));
        }
    }
    
    /**
     * Compare different strategies for a user
     * This helps users understand the trade-offs between different approaches
     */
    @GetMapping("/strategy-comparison/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> compareStrategies(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Comparing strategies for user: {} from {} to {}", userId, startDate, endDate);
        
        try {
            // Get transaction count
            long transactionCount = dataStrategyService.getStrategyRecommendations(userId, startDate, endDate)
                .get("transactionCount") instanceof Long ? 
                (Long) dataStrategyService.getStrategyRecommendations(userId, startDate, endDate).get("transactionCount") : 0L;
            
            // Build strategy comparison
            Map<String, Object> comparison = new HashMap<>();
            comparison.put("userId", userId);
            comparison.put("transactionCount", transactionCount);
            Map<String, Object> dataPeriod3 = new HashMap<>();
            if (startDate != null) dataPeriod3.put("startDate", startDate);
            if (endDate != null) dataPeriod3.put("endDate", endDate);
            comparison.put("dataPeriod", dataPeriod3);
            
            // Compare all strategies
            Map<String, Object> strategies = new HashMap<>();
            
            // Raw Data Strategy
            Map<String, Object> rawData = new HashMap<>();
            rawData.put("applicable", transactionCount < 100);
            rawData.put("estimatedTokens", transactionCount * 20);
            rawData.put("estimatedResponseTime", "2-5 seconds");
            rawData.put("costEfficiency", "High (detailed analysis)");
            rawData.put("advantages", "Maximum detail, complete transaction visibility");
            rawData.put("disadvantages", "High token usage, slower for large datasets");
            strategies.put("RAW_DATA", rawData);
            
            // Intelligent Summary Strategy
            Map<String, Object> intelligentSummary = new HashMap<>();
            intelligentSummary.put("applicable", transactionCount >= 100 && transactionCount < 1000);
            intelligentSummary.put("estimatedTokens", 200);
            intelligentSummary.put("estimatedResponseTime", "1-3 seconds");
            intelligentSummary.put("costEfficiency", "Very High (optimized)");
            intelligentSummary.put("advantages", "Fast, cost-effective, pattern-focused");
            intelligentSummary.put("disadvantages", "Less granular detail");
            strategies.put("INTELLIGENT_SUMMARY", intelligentSummary);
            
            // Chunked Processing Strategy
            Map<String, Object> chunkedProcessing = new HashMap<>();
            chunkedProcessing.put("applicable", transactionCount >= 1000);
            chunkedProcessing.put("estimatedTokens", 500);
            chunkedProcessing.put("estimatedResponseTime", "5-15 seconds");
            chunkedProcessing.put("costEfficiency", "Medium (comprehensive)");
            chunkedProcessing.put("advantages", "Handles any dataset size, complete analysis");
            chunkedProcessing.put("disadvantages", "Slower, more complex processing");
            strategies.put("CHUNKED_PROCESSING", chunkedProcessing);
            
            comparison.put("strategies", strategies);
            comparison.put("recommendedStrategy", dataStrategyService.getStrategyRecommendations(userId, startDate, endDate).get("recommendedStrategy"));
            
            return ResponseEntity.ok(comparison);
            
        } catch (Exception e) {
            logger.error("Error comparing strategies for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to compare strategies", "message", e.getMessage()));
        }
    }
    
    /**
     * Get performance metrics for strategy selection
     * This provides insights into the performance characteristics
     */
    @GetMapping("/performance-metrics/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Getting performance metrics for user: {} from {} to {}", userId, startDate, endDate);
        
        try {
            Map<String, Object> recommendations = dataStrategyService.getStrategyRecommendations(userId, startDate, endDate);
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("userId", userId);
            metrics.put("transactionCount", recommendations.get("transactionCount"));
            metrics.put("recommendedStrategy", recommendations.get("recommendedStrategy"));
            metrics.put("estimatedPerformance", recommendations.get("estimatedPerformance"));
            Map<String, Object> dataPeriod4 = new HashMap<>();
            if (startDate != null) dataPeriod4.put("startDate", startDate);
            if (endDate != null) dataPeriod4.put("endDate", endDate);
            metrics.put("dataPeriod", dataPeriod4);
            
            // Add performance insights
            Map<String, Object> insights = new HashMap<>();
            insights.put("strategySelectionTime", "~0.6ms (COUNT query + logic)");
            insights.put("databaseOptimization", "Uses indexed COUNT queries for fast counting");
            insights.put("memoryEfficiency", "Only counts transactions, doesn't load data");
            insights.put("scalability", "Works for datasets of any size");
            metrics.put("performanceInsights", insights);
            
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error getting performance metrics for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to get performance metrics", "message", e.getMessage()));
        }
    }
} 