package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.service.LLMService;
import com.expensetracker.expensetracker.service.AIAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Analytics Controller - LLM-First Approach
 * 
 * This controller implements intelligent strategies for handling large datasets:
 * 1. Data Pre-aggregation: Summarizes data before sending to LLM
 * 2. Chunked Processing: Processes large datasets in manageable chunks
 * 3. Pattern-Based Analysis: Focuses on patterns rather than raw data
 * 4. Progressive Analysis: Builds insights progressively
 * 
 * Architecture: User Request → Data Summarization → LLM Analysis → Intelligent Insights
 */
@RestController
@RequestMapping("/api/ai-analytics")
@CrossOrigin(origins = "*")
public class AIAnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AIAnalyticsController.class);
    
    @Autowired
    private DataController dataController;
    
    @Autowired
    private AIAnalysisService aiAnalysisService;
    
    @Autowired
    private LLMService llmService;
    
    // ========================================================================
    // INTELLIGENT ANALYTICS ENDPOINTS (LARGE DATASET HANDLING)
    // ========================================================================
    
    /**
     * Generate comprehensive spending insights using intelligent data summarization
     * This endpoint handles large datasets by pre-aggregating data before LLM analysis
     */
    @GetMapping("/spending-insights/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> generateSpendingInsights(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "monthly") String granularity) {
        
        logger.info("Generating spending insights for user: {} with granularity: {}", userId, granularity);
        
        try {
            // Step 1: Get intelligent summary (pre-aggregated data)
            ResponseEntity<Map<String, Object>> summaryResponse = dataController.getIntelligentSummary(
                userId, startDate, endDate, granularity);
            
            if (!summaryResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch data summary"));
            }
            
            Map<String, Object> summaryData = summaryResponse.getBody();
            
            // Step 2: Generate LLM insights using summarized data
            String insights = llmService.generateSpendingInsights(summaryData);
            
            // Step 3: Build comprehensive response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("insights", insights);
            response.put("dataSummary", summaryData);
            response.put("analysisType", "intelligent_summary");
            response.put("granularity", granularity);
            response.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating spending insights for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate insights", "message", e.getMessage()));
        }
    }
    
    /**
     * Generate spending patterns analysis using pattern-based approach
     * Focuses on spending patterns rather than individual transactions
     */
    @GetMapping("/spending-patterns/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> analyzeSpendingPatterns(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int topTransactionsLimit) {
        
        logger.info("Analyzing spending patterns for user: {} with limit: {}", userId, topTransactionsLimit);
        
        try {
            // Step 1: Get spending patterns (pre-processed patterns)
            ResponseEntity<Map<String, Object>> patternsResponse = dataController.getSpendingPatterns(
                userId, startDate, endDate, topTransactionsLimit);
            
            if (!patternsResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch spending patterns"));
            }
            
            Map<String, Object> patternsData = patternsResponse.getBody();
            
            // Step 2: Generate LLM analysis of patterns
            String patternAnalysis = llmService.generateCategoryAnalysis(patternsData, Map.of("userId", userId));
            
            // Step 3: Build response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("patternAnalysis", patternAnalysis);
            response.put("patternsData", patternsData);
            response.put("analysisType", "pattern_based");
            response.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error analyzing spending patterns for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to analyze patterns", "message", e.getMessage()));
        }
    }
    
    /**
     * Generate progressive analysis using chunked data processing
     * Processes large datasets in chunks and builds insights progressively
     */
    @GetMapping("/progressive-analysis/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> generateProgressiveAnalysis(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "50") int chunkSize) {
        
        logger.info("Generating progressive analysis for user: {} with chunk size: {}", userId, chunkSize);
        
        try {
            // Step 1: Get first chunk to understand data structure
            ResponseEntity<Map<String, Object>> firstChunkResponse = dataController.getChunkedTransactions(
                userId, startDate, endDate, chunkSize, 0);
            
            if (!firstChunkResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch data chunks"));
            }
            
            Map<String, Object> firstChunk = firstChunkResponse.getBody();
            int totalChunks = (Integer) firstChunk.get("totalChunks");
            
            // Step 2: Process chunks progressively
            StringBuilder progressiveAnalysis = new StringBuilder();
            Map<String, Object> aggregatedInsights = new HashMap<>();
            
            for (int chunkIndex = 0; chunkIndex < Math.min(totalChunks, 5); chunkIndex++) { // Limit to 5 chunks for performance
                ResponseEntity<Map<String, Object>> chunkResponse = dataController.getChunkedTransactions(
                    userId, startDate, endDate, chunkSize, chunkIndex);
                
                if (chunkResponse.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> chunkData = chunkResponse.getBody();
                    
                    // Analyze this chunk
                    String chunkAnalysis = llmService.generateSpendingInsights(chunkData);
                    progressiveAnalysis.append("Chunk ").append(chunkIndex + 1).append(" Analysis:\n")
                        .append(chunkAnalysis).append("\n\n");
                    
                    // Aggregate insights
                    aggregateChunkInsights(aggregatedInsights, chunkData);
                }
            }
            
            // Step 3: Generate final comprehensive analysis
            String finalAnalysis = llmService.generatePersonalizedRecommendations(aggregatedInsights, "comprehensive");
            
            // Step 4: Build response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("progressiveAnalysis", progressiveAnalysis.toString());
            response.put("finalAnalysis", finalAnalysis);
            response.put("aggregatedInsights", aggregatedInsights);
            response.put("chunksProcessed", Math.min(totalChunks, 5));
            response.put("totalChunks", totalChunks);
            response.put("analysisType", "progressive_chunked");
            response.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating progressive analysis for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate progressive analysis", "message", e.getMessage()));
        }
    }
    
    /**
     * Generate monthly/yearly comparison analysis
     * Compares spending across different periods using summarized data
     */
    @GetMapping("/comparison-analysis/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> generateComparisonAnalysis(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate currentEndDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate previousEndDate,
            @RequestParam(defaultValue = "monthly") String granularity) {
        
        logger.info("Generating comparison analysis for user: {} comparing {} to {}", 
                   userId, currentStartDate, previousStartDate);
        
        try {
            // Step 1: Get current period summary
            ResponseEntity<Map<String, Object>> currentResponse = dataController.getIntelligentSummary(
                userId, currentStartDate, currentEndDate, granularity);
            
            // Step 2: Get previous period summary
            ResponseEntity<Map<String, Object>> previousResponse = dataController.getIntelligentSummary(
                userId, previousStartDate, previousEndDate, granularity);
            
            if (!currentResponse.getStatusCode().is2xxSuccessful() || 
                !previousResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch comparison data"));
            }
            
            Map<String, Object> currentData = currentResponse.getBody();
            Map<String, Object> previousData = previousResponse.getBody();
            
            // Step 3: Generate comparison analysis
            String comparisonAnalysis = llmService.generateComparisonAnalysis(
                currentData, previousData, "period_comparison");
            
            // Step 4: Build response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("comparisonAnalysis", comparisonAnalysis);
            response.put("currentPeriod", currentData);
            response.put("previousPeriod", previousData);
            response.put("currentPeriodRange", Map.of("startDate", currentStartDate, "endDate", currentEndDate));
            response.put("previousPeriodRange", Map.of("startDate", previousStartDate, "endDate", previousEndDate));
            response.put("analysisType", "period_comparison");
            response.put("granularity", granularity);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating comparison analysis for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate comparison analysis", "message", e.getMessage()));
        }
    }
    
    /**
     * Generate financial health score and recommendations
     * Uses intelligent data summarization for comprehensive health assessment
     */
    @GetMapping("/financial-health/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> generateFinancialHealthScore(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Generating financial health score for user: {}", userId);
        
        try {
            // Step 1: Get comprehensive user data
            ResponseEntity<Map<String, Object>> userDataResponse = dataController.getUserAnalyticsData(
                userId, startDate, endDate);
            
            if (!userDataResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch user data"));
            }
            
            Map<String, Object> userData = userDataResponse.getBody();
            
            // Step 2: Generate financial health score
            Map<String, Object> healthScore = llmService.generateFinancialHealthScore(userData);
            
            // Step 3: Generate actionable steps
            String actionableSteps = llmService.generateActionableSteps(userData, "high");
            
            // Step 4: Build response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("financialHealthScore", healthScore);
            response.put("actionableSteps", actionableSteps);
            response.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            response.put("analysisType", "financial_health");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating financial health score for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate financial health score", "message", e.getMessage()));
        }
    }
    
    /**
     * Generate conversational financial advice
     * Allows users to ask specific questions about their finances
     */
    @PostMapping("/conversational-advice/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> generateConversationalAdvice(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {
        
        String userQuestion = request.get("question");
        logger.info("Generating conversational advice for user: {} - Question: {}", userId, userQuestion);
        
        try {
            // Step 1: Get intelligent summary for context
            ResponseEntity<Map<String, Object>> summaryResponse = dataController.getIntelligentSummary(
                userId, null, null, "monthly");
            
            if (!summaryResponse.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to fetch user data"));
            }
            
            Map<String, Object> userData = summaryResponse.getBody();
            
            // Step 2: Generate conversational advice
            String advice = llmService.generateConversationalAdvice(userData, userQuestion);
            
            // Step 3: Build response
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("userQuestion", userQuestion);
            response.put("advice", advice);
            response.put("analysisType", "conversational");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error generating conversational advice for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to generate advice", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // HELPER METHODS
    // ========================================================================
    
    /**
     * Aggregate insights from multiple chunks
     */
    private void aggregateChunkInsights(Map<String, Object> aggregatedInsights, Map<String, Object> chunkData) {
        // This is a simple aggregation - in a real implementation, you might want more sophisticated aggregation
        if (aggregatedInsights.isEmpty()) {
            aggregatedInsights.putAll(chunkData);
        } else {
            // Merge transaction counts
            int currentCount = (Integer) aggregatedInsights.getOrDefault("totalTransactions", 0);
            int chunkCount = (Integer) chunkData.getOrDefault("totalTransactions", 0);
            aggregatedInsights.put("totalTransactions", currentCount + chunkCount);
            
            // Add chunk insights to a list
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chunkInsights = (List<Map<String, Object>>) aggregatedInsights.getOrDefault("chunkInsights", List.of());
            chunkInsights.add(chunkData);
            aggregatedInsights.put("chunkInsights", chunkInsights);
        }
    }
} 