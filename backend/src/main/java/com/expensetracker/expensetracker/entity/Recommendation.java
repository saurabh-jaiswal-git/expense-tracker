package com.expensetracker.expensetracker.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@EntityListeners(AuditingEntityListener.class)
public class Recommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false)
    private RecommendationType recommendationType;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    
    @Column(name = "action_items", columnDefinition = "TEXT")
    private String actionItems; // JSON string containing actionable steps
    
    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Column(name = "estimated_savings")
    private Double estimatedSavings; // Estimated amount that can be saved
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // AI confidence in the recommendation (0.0 to 1.0)
    
    @Column(name = "category_id")
    private Long categoryId; // Related category if applicable
    
    @Column(name = "is_implemented")
    private Boolean isImplemented = false;
    
    @Column(name = "implementation_date")
    private LocalDateTime implementationDate;
    
    @Column(name = "actual_savings")
    private Double actualSavings; // Actual savings after implementation
    
    @Column(name = "model_used")
    private String modelUsed; // Which LLM model was used
    
    @Column(name = "prompt_used", columnDefinition = "TEXT")
    private String promptUsed; // The prompt that was sent to the LLM
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public Recommendation() {}
    
    public Recommendation(User user, RecommendationType recommendationType, String title, String description) {
        this.user = user;
        this.recommendationType = recommendationType;
        this.title = title;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public RecommendationType getRecommendationType() {
        return recommendationType;
    }
    
    public void setRecommendationType(RecommendationType recommendationType) {
        this.recommendationType = recommendationType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getActionItems() {
        return actionItems;
    }
    
    public void setActionItems(String actionItems) {
        this.actionItems = actionItems;
    }
    
    public Priority getPriority() {
        return priority;
    }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public Double getEstimatedSavings() {
        return estimatedSavings;
    }
    
    public void setEstimatedSavings(Double estimatedSavings) {
        this.estimatedSavings = estimatedSavings;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public Boolean getIsImplemented() {
        return isImplemented;
    }
    
    public void setIsImplemented(Boolean isImplemented) {
        this.isImplemented = isImplemented;
    }
    
    public LocalDateTime getImplementationDate() {
        return implementationDate;
    }
    
    public void setImplementationDate(LocalDateTime implementationDate) {
        this.implementationDate = implementationDate;
    }
    
    public Double getActualSavings() {
        return actualSavings;
    }
    
    public void setActualSavings(Double actualSavings) {
        this.actualSavings = actualSavings;
    }
    
    public String getModelUsed() {
        return modelUsed;
    }
    
    public void setModelUsed(String modelUsed) {
        this.modelUsed = modelUsed;
    }
    
    public String getPromptUsed() {
        return promptUsed;
    }
    
    public void setPromptUsed(String promptUsed) {
        this.promptUsed = promptUsed;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // Recommendation Type Enum
    public enum RecommendationType {
        BUDGET_OPTIMIZATION,
        SPENDING_REDUCTION,
        SAVINGS_STRATEGY,
        INVESTMENT_ADVICE,
        DEBT_MANAGEMENT,
        EXPENSE_CATEGORIZATION,
        SUBSCRIPTION_OPTIMIZATION,
        LIFESTYLE_ADJUSTMENT
    }
    
    // Priority Enum
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
} 