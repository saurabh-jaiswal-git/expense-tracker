package com.expensetracker.expensetracker.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_analysis")
@EntityListeners(AuditingEntityListener.class)
public class AIAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false)
    private AnalysisType analysisType;
    
    @Column(name = "analysis_data", columnDefinition = "TEXT")
    private String analysisData; // JSON string containing analysis results
    
    @Column(name = "insights", columnDefinition = "TEXT")
    private String insights; // Human-readable insights
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // AI confidence in the analysis (0.0 to 1.0)
    
    @Column(name = "model_used")
    private String modelUsed; // Which LLM model was used
    
    @Column(name = "prompt_used", columnDefinition = "TEXT")
    private String promptUsed; // The prompt that was sent to the LLM
    
    @Column(name = "tokens_used")
    private Integer tokensUsed; // Number of tokens consumed
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs; // Time taken to process the analysis
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // Constructors
    public AIAnalysis() {}
    
    public AIAnalysis(User user, AnalysisType analysisType, String analysisData, String insights) {
        this.user = user;
        this.analysisType = analysisType;
        this.analysisData = analysisData;
        this.insights = insights;
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
    
    public AnalysisType getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(AnalysisType analysisType) {
        this.analysisType = analysisType;
    }
    
    public String getAnalysisData() {
        return analysisData;
    }
    
    public void setAnalysisData(String analysisData) {
        this.analysisData = analysisData;
    }
    
    public String getInsights() {
        return insights;
    }
    
    public void setInsights(String insights) {
        this.insights = insights;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
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
    
    public Integer getTokensUsed() {
        return tokensUsed;
    }
    
    public void setTokensUsed(Integer tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
    
    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }
    
    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
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
    
    // Analysis Type Enum
    public enum AnalysisType {
        SPENDING_PATTERN,
        BUDGET_ANALYSIS,
        SAVINGS_OPPORTUNITY,
        CATEGORY_ANALYSIS,
        ANOMALY_DETECTION,
        TREND_ANALYSIS,
        RECOMMENDATION_GENERATION
    }
} 