package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.entity.User;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import com.expensetracker.expensetracker.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

/**
 * REST Controller for transaction management
 * Provides CRUD operations for financial transactions
 */
@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all transactions for a user with pagination
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<Transaction>> getUserTransactions(
            @PathVariable @Positive Long userId,
            Pageable pageable) {
        try {
            logger.info("Fetching transactions for user {} with pagination", userId);
            Page<Transaction> transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error fetching transactions for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get a specific transaction by ID
     * @param transactionId Transaction ID
     * @return Transaction details
     */
    @GetMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> getTransaction(@PathVariable @Positive Long transactionId) {
        try {
            logger.info("Fetching transaction with ID: {}", transactionId);
            Optional<Transaction> transaction = transactionRepository.findById(transactionId);
            return transaction.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching transaction {}: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Create a new transaction
     * @param request Transaction creation request
     * @return Created transaction
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        try {
            logger.info("Creating new transaction for user {}: {}", request.getUserId(), request.getDescription());
            
            // Validate user exists
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

            Transaction transaction = Transaction.builder()
                    .user(user)
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .transactionType(request.getTransactionType())
                    .transactionDate(request.getTransactionDate())
                    .transactionTime(request.getTransactionTime())
                    .description(request.getDescription())
                    .notes(request.getNotes())
                    .sourceType(Transaction.SourceType.MANUAL)
                    .build();

            Transaction savedTransaction = transactionRepository.save(transaction);
            logger.info("Successfully created transaction with ID: {}", savedTransaction.getId());
            return ResponseEntity.ok(savedTransaction);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for transaction creation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating transaction: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Update an existing transaction
     * @param transactionId Transaction ID
     * @param request Update request
     * @return Updated transaction
     */
    @PutMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable @Positive Long transactionId,
            @Valid @RequestBody UpdateTransactionRequest request) {
        try {
            logger.info("Updating transaction with ID: {}", transactionId);
            
            Optional<Transaction> existingTransaction = transactionRepository.findById(transactionId);
            if (existingTransaction.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Transaction transaction = existingTransaction.get();
            
            // Update fields if provided
            if (request.getAmount() != null) {
                transaction.setAmount(request.getAmount());
            }
            if (request.getCurrency() != null) {
                transaction.setCurrency(request.getCurrency());
            }
            if (request.getTransactionType() != null) {
                transaction.setTransactionType(request.getTransactionType());
            }
            if (request.getTransactionDate() != null) {
                transaction.setTransactionDate(request.getTransactionDate());
            }
            if (request.getTransactionTime() != null) {
                transaction.setTransactionTime(request.getTransactionTime());
            }
            if (request.getDescription() != null) {
                transaction.setDescription(request.getDescription());
            }
            if (request.getNotes() != null) {
                transaction.setNotes(request.getNotes());
            }

            Transaction updatedTransaction = transactionRepository.save(transaction);
            logger.info("Successfully updated transaction with ID: {}", transactionId);
            return ResponseEntity.ok(updatedTransaction);
        } catch (Exception e) {
            logger.error("Error updating transaction {}: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a transaction
     * @param transactionId Transaction ID
     * @return Success response
     */
    @DeleteMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTransaction(@PathVariable @Positive Long transactionId) {
        try {
            logger.info("Deleting transaction with ID: {}", transactionId);
            
            if (!transactionRepository.existsById(transactionId)) {
                return ResponseEntity.notFound().build();
            }

            transactionRepository.deleteById(transactionId);
            logger.info("Successfully deleted transaction with ID: {}", transactionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting transaction {}: {}", transactionId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get transaction summary for a user
     * @param userId User ID
     * @param startDate Start date for summary
     * @param endDate End date for summary
     * @return Transaction summary
     */
    @GetMapping("/user/{userId}/summary")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(
            @PathVariable @Positive Long userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        try {
            logger.info("Fetching transaction summary for user {} from {} to {}", userId, startDate, endDate);
            
            List<Transaction> transactions;
            if (startDate != null && endDate != null) {
                transactions = transactionRepository.findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
                        userId, startDate, endDate);
            } else {
                transactions = transactionRepository.findByUserIdOrderByTransactionDateDesc(userId);
            }

            BigDecimal totalIncome = transactions.stream()
                    .filter(Transaction::isIncome)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalExpense = transactions.stream()
                    .filter(Transaction::isExpense)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> summary = new HashMap<>();
            summary.put("totalTransactions", transactions.size());
            summary.put("totalIncome", totalIncome);
            summary.put("totalExpense", totalExpense);
            summary.put("netAmount", totalIncome.subtract(totalExpense));
            
            Map<String, Object> period = new HashMap<>();
            period.put("startDate", startDate);
            period.put("endDate", endDate);
            summary.put("period", period);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error fetching transaction summary for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search transactions by description
     * @param userId User ID
     * @param query Search query
     * @param pageable Pagination parameters
     * @return Page of matching transactions
     */
    @GetMapping("/user/{userId}/search")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<Transaction>> searchTransactions(
            @PathVariable @Positive Long userId,
            @RequestParam @NotNull String query,
            Pageable pageable) {
        try {
            logger.info("Searching transactions for user {} with query: {}", userId, query);
            Page<Transaction> transactions = transactionRepository.findByUserIdAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(
                    userId, query, pageable);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error searching transactions for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Request DTOs
    public static class CreateTransactionRequest {
        @NotNull
        @Positive
        private Long userId;

        @NotNull
        private BigDecimal amount;

        private String currency = "INR";

        @NotNull
        private Transaction.TransactionType transactionType;

        @NotNull
        private LocalDate transactionDate;

        private java.time.LocalTime transactionTime;

        @NotNull
        private String description;

        private String notes;

        // Getters and setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public Transaction.TransactionType getTransactionType() { return transactionType; }
        public void setTransactionType(Transaction.TransactionType transactionType) { this.transactionType = transactionType; }

        public LocalDate getTransactionDate() { return transactionDate; }
        public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

        public java.time.LocalTime getTransactionTime() { return transactionTime; }
        public void setTransactionTime(java.time.LocalTime transactionTime) { this.transactionTime = transactionTime; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class UpdateTransactionRequest {
        private BigDecimal amount;
        private String currency;
        private Transaction.TransactionType transactionType;
        private LocalDate transactionDate;
        private java.time.LocalTime transactionTime;
        private String description;
        private String notes;

        // Getters and setters
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public Transaction.TransactionType getTransactionType() { return transactionType; }
        public void setTransactionType(Transaction.TransactionType transactionType) { this.transactionType = transactionType; }

        public LocalDate getTransactionDate() { return transactionDate; }
        public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

        public java.time.LocalTime getTransactionTime() { return transactionTime; }
        public void setTransactionTime(java.time.LocalTime transactionTime) { this.transactionTime = transactionTime; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
} 