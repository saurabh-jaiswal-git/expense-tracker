package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.*;
import com.expensetracker.expensetracker.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Data Controller for Analytics and LLM Consumption
 * 
 * Provides simple, fast endpoints for raw data retrieval.
 * These endpoints feed data to LLM services for intelligent analysis.
 * 
 * Architecture: User Data → Data Endpoints → LLM Service → AI Insights
 */
@RestController
@RequestMapping("/api/data")
@CrossOrigin(origins = "*")
public class DataController {
    
    private static final Logger logger = LoggerFactory.getLogger(DataController.class);
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private BudgetRepository budgetRepository;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BudgetCategoryRepository budgetCategoryRepository;
    
    @Autowired
    private UserCategoryRepository userCategoryRepository;
    
    // ========================================================================
    // TRANSACTION DATA ENDPOINTS
    // ========================================================================
    
    /**
     * Get user transactions with optional date filtering
     */
    @GetMapping("/transactions/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String category) {
        
        logger.info("Fetching transactions for user: {} with filters - startDate: {}, endDate: {}, category: {}", 
                   userId, startDate, endDate, category);
        
        try {
            List<Transaction> transactions;
            
            // Use appropriate repository method based on filters
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            // Apply additional filters
            if (category != null && !category.trim().isEmpty()) {
                transactions = transactions.stream()
                    .filter(t -> t.getCategory() != null && 
                               t.getCategory().getName().toLowerCase().contains(category.toLowerCase()))
                    .collect(Collectors.toList());
            }
            
            // Convert to LLM-friendly format
            List<Map<String, Object>> transactionData = transactions.stream()
                .map(this::convertTransactionToMap)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("totalTransactions", transactionData.size());
            response.put("totalAmount", transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            response.put("transactions", transactionData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching transactions for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch transactions", "message", e.getMessage()));
        }
    }
    
    /**
     * Get spending summary by period (daily, weekly, monthly)
     */
    @GetMapping("/spending-summary/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getSpendingSummary(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "monthly") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching spending summary for user: {} with period: {}", userId, period);
        
        try {
            List<Transaction> transactions;
            
            // Use appropriate repository method based on filters
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            Map<String, Object> summary = generateSpendingSummary(transactions, period);
            summary.put("userId", userId);
            summary.put("period", period);
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            logger.error("Error fetching spending summary for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch spending summary", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // CATEGORY DATA ENDPOINTS
    // ========================================================================
    
    /**
     * Get user categories
     */
    @GetMapping("/categories/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserCategories(@PathVariable Long userId) {
        
        logger.info("Fetching categories for user: {}", userId);
        
        try {
            List<Category> categories = categoryRepository.findAll();
            List<UserCategory> userCategories = userCategoryRepository.findByUserId(userId);
            
            // Get user's custom categories
            List<Map<String, Object>> categoryData = categories.stream()
                .map(this::convertCategoryToMap)
                .collect(Collectors.toList());
            
            // Add user-specific category data
            Map<Long, UserCategory> userCategoryMap = userCategories.stream()
                .collect(Collectors.toMap(uc -> uc.getParentCategory().getId(), uc -> uc));
            
            categoryData.forEach(cat -> {
                Long categoryId = (Long) cat.get("id");
                UserCategory userCat = userCategoryMap.get(categoryId);
                if (userCat != null) {
                    cat.put("isActive", userCat.isActive());
                    cat.put("budgetLimit", null); // UserCategory doesn't have budgetLimit field
                } else {
                    cat.put("isActive", true);
                    cat.put("budgetLimit", null);
                }
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("categories", categoryData);
            response.put("totalCategories", categoryData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching categories for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch categories", "message", e.getMessage()));
        }
    }
    
    /**
     * Get category spending breakdown
     */
    @GetMapping("/category-breakdown/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getCategoryBreakdown(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching category breakdown for user: {}", userId);
        
        try {
            List<Transaction> transactions;
            
            // Use appropriate repository method based on filters
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            // Group by category
            Map<Category, BigDecimal> categorySpending = transactions.stream()
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                    Transaction::getCategory,
                    Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
                ));
            
            List<Map<String, Object>> breakdown = categorySpending.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> catData = new HashMap<>();
                    catData.put("categoryId", entry.getKey().getId());
                    catData.put("categoryName", entry.getKey().getName());
                    catData.put("categoryColor", entry.getKey().getColor());
                    catData.put("totalSpent", entry.getValue());
                    catData.put("transactionCount", transactions.stream()
                        .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(entry.getKey().getId()))
                        .count());
                    return catData;
                })
                .sorted((a, b) -> ((BigDecimal) b.get("totalSpent")).compareTo((BigDecimal) a.get("totalSpent")))
                .collect(Collectors.toList());
            
            BigDecimal totalSpent = breakdown.stream()
                .map(cat -> (BigDecimal) cat.get("totalSpent"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Add percentage to each category
            breakdown.forEach(cat -> {
                BigDecimal spent = (BigDecimal) cat.get("totalSpent");
                double percentage = totalSpent.compareTo(BigDecimal.ZERO) > 0 ? 
                    spent.divide(totalSpent, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue() : 0;
                cat.put("percentage", Math.round(percentage * 100.0) / 100.0);
            });
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("totalSpent", totalSpent);
            response.put("categoryBreakdown", breakdown);
            response.put("topCategory", breakdown.isEmpty() ? null : breakdown.get(0));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching category breakdown for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch category breakdown", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // BUDGET DATA ENDPOINTS
    // ========================================================================
    
    /**
     * Get user budgets
     */
    @GetMapping("/budgets/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserBudgets(@PathVariable Long userId) {
        
        logger.info("Fetching budgets for user: {}", userId);
        
        try {
            List<Budget> budgets = budgetRepository.findByUserIdOrderByYearMonthDesc(userId);
            
            List<Map<String, Object>> budgetData = budgets.stream()
                .map(this::convertBudgetToMap)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("budgets", budgetData);
            response.put("totalBudgets", budgetData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching budgets for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch budgets", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // GOAL DATA ENDPOINTS
    // ========================================================================
    
    /**
     * Get user goals
     */
    @GetMapping("/goals/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserGoals(@PathVariable Long userId) {
        
        logger.info("Fetching goals for user: {}", userId);
        
        try {
            List<Goal> goals = goalRepository.findByUserId(userId);
            
            List<Map<String, Object>> goalData = goals.stream()
                .map(this::convertGoalToMap)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("goals", goalData);
            response.put("totalGoals", goalData.size());
            response.put("activeGoals", goalData.stream()
                .filter(goal -> "ACTIVE".equals(goal.get("status")))
                .count());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching goals for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch goals", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // COMPOSITE DATA ENDPOINTS
    // ========================================================================
    
    /**
     * Get comprehensive user data for LLM analysis
     */
    @GetMapping("/user-analytics-data/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getUserAnalyticsData(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        logger.info("Fetching comprehensive analytics data for user: {}", userId);
        
        try {
            // Get user info
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get all data
            List<Transaction> transactions;
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            List<Budget> budgets = budgetRepository.findByUserIdOrderByYearMonthDesc(userId);
            List<Goal> goals = goalRepository.findByUserId(userId);
            List<Category> categories = categoryRepository.findAll();
            
            // Build comprehensive response
            Map<String, Object> response = new HashMap<>();
            response.put("user", convertUserToMap(user));
            response.put("transactions", transactions.stream().map(this::convertTransactionToMap).collect(Collectors.toList()));
            response.put("budgets", budgets.stream().map(this::convertBudgetToMap).collect(Collectors.toList()));
            response.put("goals", goals.stream().map(this::convertGoalToMap).collect(Collectors.toList()));
            response.put("categories", categories.stream().map(this::convertCategoryToMap).collect(Collectors.toList()));
            
            // Add summary statistics
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalTransactions", transactions.size());
            summary.put("totalSpent", transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            summary.put("totalBudgets", budgets.size());
            summary.put("totalGoals", goals.size());
            summary.put("activeGoals", goals.stream().filter(g -> g.getStatus() == GoalStatus.ACTIVE).count());
            summary.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching analytics data for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch analytics data", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // INTELLIGENT DATA SUMMARIZATION ENDPOINTS (LLM-OPTIMIZED)
    // ========================================================================
    
    /**
     * Get intelligent spending summary for LLM analysis (handles large datasets)
     * This endpoint pre-aggregates data to avoid sending thousands of transactions to LLM
     */
    @GetMapping("/intelligent-summary/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getIntelligentSummary(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "monthly") String granularity) {
        
        logger.info("Fetching intelligent summary for user: {} with granularity: {}", userId, granularity);
        
        try {
            List<Transaction> transactions;
            
            // Get transactions for the period
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            // Create intelligent summary
            Map<String, Object> summary = createIntelligentSummary(transactions, granularity);
            summary.put("userId", userId);
            summary.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            summary.put("granularity", granularity);
            summary.put("totalTransactions", transactions.size());
            
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            logger.error("Error fetching intelligent summary for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch intelligent summary", "message", e.getMessage()));
        }
    }
    
    /**
     * Get spending patterns for LLM analysis (top transactions + patterns)
     */
    @GetMapping("/spending-patterns/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getSpendingPatterns(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int topTransactionsLimit) {
        
        logger.info("Fetching spending patterns for user: {} with limit: {}", userId, topTransactionsLimit);
        
        try {
            List<Transaction> transactions;
            
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            Map<String, Object> patterns = createSpendingPatterns(transactions, topTransactionsLimit);
            patterns.put("userId", userId);
            patterns.put("dataPeriod", Map.of("startDate", startDate, "endDate", endDate));
            
            return ResponseEntity.ok(patterns);
            
        } catch (Exception e) {
            logger.error("Error fetching spending patterns for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch spending patterns", "message", e.getMessage()));
        }
    }
    
    /**
     * Get chunked transaction data for large datasets
     * This allows LLM to process data in manageable chunks
     */
    @GetMapping("/chunked-transactions/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getChunkedTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "50") int chunkSize,
            @RequestParam(defaultValue = "0") int chunkIndex) {
        
        logger.info("Fetching chunked transactions for user: {} - chunk {} of size {}", userId, chunkIndex, chunkSize);
        
        try {
            List<Transaction> allTransactions;
            
            if (startDate != null && endDate != null) {
                allTransactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(userId, startDate, endDate);
            } else {
                allTransactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }
            
            // Calculate chunking
            int totalTransactions = allTransactions.size();
            int totalChunks = (int) Math.ceil((double) totalTransactions / chunkSize);
            int startIndex = chunkIndex * chunkSize;
            int endIndex = Math.min(startIndex + chunkSize, totalTransactions);
            
            List<Transaction> chunkedTransactions = allTransactions.subList(startIndex, endIndex);
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("chunkIndex", chunkIndex);
            response.put("chunkSize", chunkSize);
            response.put("totalChunks", totalChunks);
            response.put("totalTransactions", totalTransactions);
            response.put("transactionsInChunk", chunkedTransactions.size());
            response.put("hasMoreChunks", chunkIndex < totalChunks - 1);
            response.put("transactions", chunkedTransactions.stream()
                .map(this::convertTransactionToMap)
                .collect(Collectors.toList()));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching chunked transactions for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to fetch chunked transactions", "message", e.getMessage()));
        }
    }
    
    // ========================================================================
    // HELPER METHODS
    // ========================================================================
    
    private Map<String, Object> convertTransactionToMap(Transaction transaction) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", transaction.getId());
        map.put("amount", transaction.getAmount());
        map.put("description", transaction.getDescription());
        map.put("date", transaction.getTransactionDate());
        map.put("type", transaction.getTransactionType());
        map.put("category", transaction.getCategory() != null ? Map.of(
            "id", transaction.getCategory().getId(),
            "name", transaction.getCategory().getName(),
            "color", transaction.getCategory().getColor()
        ) : null);
        map.put("createdAt", transaction.getCreatedAt());
        map.put("updatedAt", transaction.getUpdatedAt());
        return map;
    }
    
    private Map<String, Object> convertCategoryToMap(Category category) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", category.getId());
        map.put("name", category.getName());
        map.put("color", category.getColor());
        map.put("icon", category.getIcon());
        map.put("description", category.getDescription());
        return map;
    }
    
    private Map<String, Object> convertBudgetToMap(Budget budget) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", budget.getId());
        map.put("yearMonth", budget.getYearMonth());
        map.put("totalBudget", budget.getTotalBudget());
        map.put("actualSpending", budget.getActualSpending());
        map.put("remainingBudget", budget.getRemainingBudget());
        map.put("status", budget.getStatus());
        map.put("spendingPercentage", budget.getSpendingPercentage());
        map.put("isActive", budget.getIsActive());
        map.put("notes", budget.getNotes());
        map.put("createdAt", budget.getCreatedAt());
        map.put("updatedAt", budget.getUpdatedAt());
        return map;
    }
    
    private Map<String, Object> convertGoalToMap(Goal goal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", goal.getId());
        map.put("name", goal.getName());
        map.put("description", goal.getDescription());
        map.put("targetAmount", goal.getTargetAmount());
        map.put("currentAmount", goal.getCurrentAmount());
        map.put("progressPercentage", goal.getProgressPercentage());
        map.put("goalType", goal.getGoalType());
        map.put("targetDate", goal.getTargetDate());
        map.put("startDate", goal.getStartDate());
        map.put("status", goal.getStatus());
        map.put("createdAt", goal.getCreatedAt());
        map.put("updatedAt", goal.getUpdatedAt());
        return map;
    }
    
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("email", user.getEmail());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("createdAt", user.getCreatedAt());
        map.put("updatedAt", user.getUpdatedAt());
        return map;
    }
    
    private Map<String, Object> generateSpendingSummary(List<Transaction> transactions, String period) {
        Map<String, Object> summary = new HashMap<>();
        
        if (transactions.isEmpty()) {
            summary.put("periods", new ArrayList<>());
            summary.put("totalSpent", BigDecimal.ZERO);
            summary.put("averageSpent", BigDecimal.ZERO);
            return summary;
        }
        
        Map<String, BigDecimal> periodSpending = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            String periodKey = getPeriodKey(transaction.getTransactionDate(), period);
            periodSpending.merge(periodKey, transaction.getAmount(), BigDecimal::add);
        }
        
        List<Map<String, Object>> periods = periodSpending.entrySet().stream()
            .map(entry -> {
                Map<String, Object> periodData = new HashMap<>();
                periodData.put("period", entry.getKey());
                periodData.put("amount", entry.getValue());
                return periodData;
            })
            .sorted(Comparator.comparing(p -> (String) p.get("period")))
            .collect(Collectors.toList());
        
        BigDecimal totalSpent = transactions.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageSpent = periods.isEmpty() ? BigDecimal.ZERO : 
            totalSpent.divide(BigDecimal.valueOf(periods.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        summary.put("periods", periods);
        summary.put("totalSpent", totalSpent);
        summary.put("averageSpent", averageSpent);
        summary.put("periodType", period);
        
        return summary;
    }
    
    private String getPeriodKey(LocalDate date, String period) {
        switch (period.toLowerCase()) {
            case "daily":
                return date.toString();
            case "weekly":
                return date.getYear() + "-W" + String.format("%02d", date.get(WeekFields.ISO.weekOfWeekBasedYear()));
            case "monthly":
                return YearMonth.from(date).toString();
            default:
                return date.toString();
        }
    }
    
    /**
     * Create intelligent summary for LLM analysis (pre-aggregates large datasets)
     */
    private Map<String, Object> createIntelligentSummary(List<Transaction> transactions, String granularity) {
        Map<String, Object> summary = new HashMap<>();
        
        if (transactions.isEmpty()) {
            summary.put("totalSpent", BigDecimal.ZERO);
            summary.put("totalIncome", BigDecimal.ZERO);
            summary.put("netAmount", BigDecimal.ZERO);
            summary.put("categoryBreakdown", new ArrayList<>());
            summary.put("periodBreakdown", new ArrayList<>());
            summary.put("topSpendingCategories", new ArrayList<>());
            summary.put("spendingTrends", new ArrayList<>());
            return summary;
        }
        
        // Basic totals
        BigDecimal totalSpent = transactions.stream()
            .filter(Transaction::isExpense)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalIncome = transactions.stream()
            .filter(Transaction::isIncome)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal netAmount = totalIncome.subtract(totalSpent);
        
        // Category breakdown (top 10 categories)
        Map<Category, BigDecimal> categorySpending = transactions.stream()
            .filter(t -> t.getCategory() != null && t.isExpense())
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
            ));
        
        List<Map<String, Object>> categoryBreakdown = categorySpending.entrySet().stream()
            .map(entry -> {
                Map<String, Object> catData = new HashMap<>();
                catData.put("categoryName", entry.getKey().getName());
                catData.put("totalSpent", entry.getValue());
                catData.put("percentage", entry.getValue().divide(totalSpent, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
                catData.put("transactionCount", transactions.stream()
                    .filter(t -> t.getCategory() != null && t.getCategory().getId().equals(entry.getKey().getId()))
                    .count());
                return catData;
            })
            .sorted((a, b) -> ((BigDecimal) b.get("totalSpent")).compareTo((BigDecimal) a.get("totalSpent")))
            .limit(10)
            .collect(Collectors.toList());
        
        // Period breakdown
        Map<String, BigDecimal> periodSpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            String periodKey = getPeriodKey(transaction.getTransactionDate(), granularity);
            if (transaction.isExpense()) {
                periodSpending.merge(periodKey, transaction.getAmount(), BigDecimal::add);
            }
        }
        
        List<Map<String, Object>> periodBreakdown = periodSpending.entrySet().stream()
            .map(entry -> {
                Map<String, Object> periodData = new HashMap<>();
                periodData.put("period", entry.getKey());
                periodData.put("amount", entry.getValue());
                return periodData;
            })
            .sorted(Comparator.comparing(p -> (String) p.get("period")))
            .collect(Collectors.toList());
        
        // Top spending categories (for quick insights)
        List<Map<String, Object>> topSpendingCategories = categoryBreakdown.stream()
            .limit(5)
            .collect(Collectors.toList());
        
        // Spending trends (simple trend analysis)
        List<Map<String, Object>> spendingTrends = new ArrayList<>();
        if (periodBreakdown.size() >= 2) {
            for (int i = 1; i < periodBreakdown.size(); i++) {
                Map<String, Object> current = periodBreakdown.get(i);
                Map<String, Object> previous = periodBreakdown.get(i - 1);
                
                BigDecimal currentAmount = (BigDecimal) current.get("amount");
                BigDecimal previousAmount = (BigDecimal) previous.get("amount");
                BigDecimal change = currentAmount.subtract(previousAmount);
                BigDecimal changePercentage = previousAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
                    change.divide(previousAmount, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100));
                
                Map<String, Object> trend = new HashMap<>();
                trend.put("period", current.get("period"));
                trend.put("amount", currentAmount);
                trend.put("change", change);
                trend.put("changePercentage", changePercentage);
                trend.put("trend", change.compareTo(BigDecimal.ZERO) > 0 ? "increasing" : 
                    change.compareTo(BigDecimal.ZERO) < 0 ? "decreasing" : "stable");
                spendingTrends.add(trend);
            }
        }
        
        summary.put("totalSpent", totalSpent);
        summary.put("totalIncome", totalIncome);
        summary.put("netAmount", netAmount);
        summary.put("categoryBreakdown", categoryBreakdown);
        summary.put("periodBreakdown", periodBreakdown);
        summary.put("topSpendingCategories", topSpendingCategories);
        summary.put("spendingTrends", spendingTrends);
        summary.put("averageDailySpending", totalSpent.divide(BigDecimal.valueOf(transactions.size()), 2, BigDecimal.ROUND_HALF_UP));
        
        return summary;
    }
    
    /**
     * Create spending patterns for LLM analysis
     */
    private Map<String, Object> createSpendingPatterns(List<Transaction> transactions, int topTransactionsLimit) {
        Map<String, Object> patterns = new HashMap<>();
        
        if (transactions.isEmpty()) {
            patterns.put("topTransactions", new ArrayList<>());
            patterns.put("spendingPatterns", new ArrayList<>());
            patterns.put("anomalies", new ArrayList<>());
            return patterns;
        }
        
        // Top transactions by amount
        List<Map<String, Object>> topTransactions = transactions.stream()
            .filter(Transaction::isExpense)
            .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
            .limit(topTransactionsLimit)
            .map(this::convertTransactionToMap)
            .collect(Collectors.toList());
        
        // Spending patterns (daily, weekly patterns)
        Map<String, BigDecimal> dailySpending = transactions.stream()
            .filter(Transaction::isExpense)
            .collect(Collectors.groupingBy(
                t -> t.getTransactionDate().getDayOfWeek().toString(),
                Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
            ));
        
        List<Map<String, Object>> spendingPatterns = dailySpending.entrySet().stream()
            .map(entry -> {
                Map<String, Object> pattern = new HashMap<>();
                pattern.put("dayOfWeek", entry.getKey());
                pattern.put("totalSpent", entry.getValue());
                pattern.put("averageSpent", entry.getValue().divide(BigDecimal.valueOf(
                    transactions.stream()
                        .filter(t -> t.getTransactionDate().getDayOfWeek().toString().equals(entry.getKey()))
                        .count()), 2, BigDecimal.ROUND_HALF_UP));
                return pattern;
            })
            .sorted((a, b) -> ((BigDecimal) b.get("totalSpent")).compareTo((BigDecimal) a.get("totalSpent")))
            .collect(Collectors.toList());
        
        // Simple anomaly detection (transactions > 2x average)
        BigDecimal averageAmount = transactions.stream()
            .filter(Transaction::isExpense)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(transactions.stream().filter(Transaction::isExpense).count()), 2, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal threshold = averageAmount.multiply(BigDecimal.valueOf(2));
        
        List<Map<String, Object>> anomalies = transactions.stream()
            .filter(t -> t.isExpense() && t.getAmount().compareTo(threshold) > 0)
            .map(this::convertTransactionToMap)
            .collect(Collectors.toList());
        
        patterns.put("topTransactions", topTransactions);
        patterns.put("spendingPatterns", spendingPatterns);
        patterns.put("anomalies", anomalies);
        patterns.put("averageTransactionAmount", averageAmount);
        patterns.put("anomalyThreshold", threshold);
        
        return patterns;
    }
} 