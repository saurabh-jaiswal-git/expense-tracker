package com.expensetracker.expensetracker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    // Transaction Details
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(length = 3)
    private String currency = "INR";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    @Column(name = "transaction_time")
    private LocalTime transactionTime;
    
    private String description;
    
    private String notes;
    
    // Categorization
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_category_id")
    private UserCategory userCategory;
    
    // Source Tracking (for future bank integration)
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private SourceType sourceType = SourceType.MANUAL;
    
    @Column(name = "source_id")
    private String sourceId; // Bank transaction ID or UPI reference
    
    @Column(name = "external_reference")
    private String externalReference; // Bank's internal reference
    
    @Column(name = "bank_name")
    private String bankName; // HDFC, SBI, etc.
    
    @Column(name = "account_number")
    private String accountNumber; // Masked account number
    
    // Data Integrity & Audit
    @Column(name = "is_immutable")
    private Boolean isImmutable = false; // Bank transactions can't be edited
    
    @Column(name = "is_recurring")
    private Boolean isRecurring = false;
    
    @Column(name = "recurring_pattern")
    private String recurringPattern; // DAILY, WEEKLY, MONTHLY, YEARLY
    
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status")
    private SyncStatus syncStatus = SyncStatus.PENDING;
    
    // Metadata
    @Column(columnDefinition = "JSON")
    private String tags; // Flexible tagging system
    
    private String location; // For future GPS integration
    
    @Column(name = "receipt_url")
    private String receiptUrl; // For receipt images
    
    // Timestamps
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Enums
    public enum TransactionType {
        EXPENSE, INCOME, TRANSFER
    }
    
    public enum SourceType {
        MANUAL, UPI, BANK, ACCOUNT_AGGREGATOR, CREDIT_CARD
    }
    
    public enum SyncStatus {
        PENDING, SYNCED, FAILED
    }
    
    // Helper methods
    public boolean isExpense() {
        return TransactionType.EXPENSE.equals(transactionType);
    }
    
    public boolean isIncome() {
        return TransactionType.INCOME.equals(transactionType);
    }
    
    public boolean isTransfer() {
        return TransactionType.TRANSFER.equals(transactionType);
    }
    
    public boolean isManualEntry() {
        return SourceType.MANUAL.equals(sourceType);
    }
    
    public boolean isBankTransaction() {
        return SourceType.BANK.equals(sourceType) || 
               SourceType.ACCOUNT_AGGREGATOR.equals(sourceType) ||
               SourceType.UPI.equals(sourceType);
    }
    
    public boolean isImmutable() {
        return isImmutable != null && isImmutable;
    }
    
    public boolean isRecurring() {
        return isRecurring != null && isRecurring;
    }
    
    public boolean isSynced() {
        return SyncStatus.SYNCED.equals(syncStatus);
    }
} 