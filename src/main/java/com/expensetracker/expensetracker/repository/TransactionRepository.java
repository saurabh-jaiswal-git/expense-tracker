package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find all transactions for a user ordered by transaction date descending
     */
    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
    
    /**
     * Find all transactions for a user with pagination
     */
    Page<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId, Pageable pageable);
    
    /**
     * Find transactions for a user within a date range
     */
    List<Transaction> findByUserIdAndTransactionDateBetweenOrderByTransactionDateDesc(
            Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Search transactions by description (case-insensitive)
     */
    Page<Transaction> findByUserIdAndDescriptionContainingIgnoreCaseOrderByTransactionDateDesc(
            Long userId, String description, Pageable pageable);
    
    /**
     * Find transactions by type for a user
     */
    List<Transaction> findByUserIdAndTransactionTypeOrderByTransactionDateDesc(
            Long userId, Transaction.TransactionType transactionType);
    
    /**
     * Find transactions by category for a user
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdAndCategoryIdOrderByTransactionDateDesc(
            @Param("userId") Long userId, @Param("categoryId") Long categoryId);
    
    /**
     * Get total count of transactions for a user
     */
    long countByUserId(Long userId);
    
    /**
     * Get total count of transactions for a user within a date range
     */
    long countByUserIdAndTransactionDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Find transactions by source type for a user
     */
    List<Transaction> findByUserIdAndSourceTypeOrderByTransactionDateDesc(
            Long userId, Transaction.SourceType sourceType);
    
    /**
     * Find recurring transactions for a user
     */
    List<Transaction> findByUserIdAndIsRecurringTrueOrderByTransactionDateDesc(Long userId);
    
    /**
     * Find transactions with high amounts (above threshold) for a user
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.amount > :threshold ORDER BY t.amount DESC")
    List<Transaction> findByUserIdAndAmountGreaterThanOrderByAmountDesc(
            @Param("userId") Long userId, @Param("threshold") java.math.BigDecimal threshold);
} 