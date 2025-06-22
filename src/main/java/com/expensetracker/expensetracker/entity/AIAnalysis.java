package com.expensetracker.expensetracker.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ai_analysis")
@EntityListeners(AuditingEntityListener.class)
public class AIAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "analysis_type", nullable = false)
    private String analysisType;
    
    @Lob
    @Column(name = "analysis_data", columnDefinition = "TEXT")
    private String analysisData; // JSON string containing analysis results
    
    @Lob
    @Column(columnDefinition = "TEXT")
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
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "is_active")
    private boolean isActive = true;
} 