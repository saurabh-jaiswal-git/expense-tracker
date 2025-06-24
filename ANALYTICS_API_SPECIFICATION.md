# Analytics API Specification

## Overview
This document defines the REST API endpoints for analytics and LLM-powered insights in the expense tracker application. All endpoints follow RESTful conventions and return JSON responses.

## Base URL
```
http://localhost:8080/api/analytics
```

## Authentication
All endpoints require HTTP Basic Authentication:
```
Authorization: Basic <base64(username:password)>
```

## Common Response Format
```json
{
  "success": true,
  "data": { ... },
  "message": "Success message",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "ANALYTICS_ERROR",
    "message": "Error description",
    "details": { ... }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## Core Analytics Endpoints

### 1. Spending Analysis

#### GET /api/analytics/spending/{userId}

**Description**: Get comprehensive spending analysis for a user

**Path Parameters**:
- `userId` (Long): User ID

**Query Parameters**:
- `period` (String, optional): Time period - `week`, `month`, `quarter`, `year` (default: `month`)
- `category` (String, optional): Filter by specific category
- `startDate` (String, optional): Start date in ISO format (YYYY-MM-DD)
- `endDate` (String, optional): End date in ISO format (YYYY-MM-DD)

**Response**:
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalSpending": 45000.00,
      "totalIncome": 50000.00,
      "netSavings": 5000.00,
      "savingsRate": 10.0,
      "period": "2024-01",
      "transactionCount": 45
    },
    "spendingByCategory": [
      {
        "categoryId": 1,
        "categoryName": "Food & Dining",
        "amount": 12000.00,
        "percentage": 26.7,
        "trend": 15.5,
        "budgetStatus": "UNDER_BUDGET",
        "budgetAmount": 8000.00,
        "budgetUtilization": 150.0
      }
    ],
    "trends": {
      "monthOverMonth": 12.3,
      "quarterOverQuarter": 8.7,
      "yearOverYear": 5.2,
      "seasonalPattern": "Summer spending typically 20% higher"
    },
    "topCategories": [
      {
        "category": "Food & Dining",
        "amount": 12000.00,
        "percentage": 26.7
      },
      {
        "category": "Transportation",
        "amount": 8000.00,
        "percentage": 17.8
      }
    ],
    "insights": [
      "Your dining out expenses have increased 30% this month",
      "You're spending 15% more on weekends than weekdays",
      "Your grocery spending is below your 3-month average"
    ],
    "recommendations": [
      {
        "title": "Reduce Dining Out",
        "description": "Consider cooking at home 3-4 times per week",
        "estimatedSavings": 2000.00,
        "priority": "HIGH"
      }
    ]
  },
  "message": "Spending analysis retrieved successfully"
}
```

### 2. Budget Performance

#### GET /api/analytics/budget/{userId}/{yearMonth}

**Description**: Get budget performance analysis for a specific month

**Path Parameters**:
- `userId` (Long): User ID
- `yearMonth` (String): Year-month in format YYYY-MM

**Response**:
```json
{
  "success": true,
  "data": {
    "budgetSummary": {
      "yearMonth": "2024-01",
      "totalBudget": 40000.00,
      "actualSpending": 35000.00,
      "remainingBudget": 5000.00,
      "utilizationPercentage": 87.5,
      "status": "UNDER_BUDGET",
      "daysRemaining": 15,
      "projectedEndOfMonthSpending": 38000.00,
      "willExceedBudget": false
    },
    "categoryBreakdown": [
      {
        "categoryId": 1,
        "categoryName": "Food & Dining",
        "budgetAmount": 8000.00,
        "actualAmount": 7500.00,
        "remainingAmount": 500.00,
        "utilizationPercentage": 93.8,
        "status": "UNDER_BUDGET",
        "trend": "decreasing"
      }
    ],
    "performanceMetrics": {
      "categoriesUnderBudget": 3,
      "categoriesOverBudget": 1,
      "categoriesOnTrack": 2,
      "totalPotentialSavings": 2000.00
    },
    "insights": [
      "You're 20% under budget this month - great job!",
      "Your entertainment budget is 85% used with 10 days remaining",
      "Food spending is trending downward this month"
    ],
    "recommendations": [
      {
        "title": "Monitor Entertainment Budget",
        "description": "You're close to exceeding your entertainment budget",
        "action": "Review remaining entertainment expenses",
        "priority": "MEDIUM"
      }
    ]
  },
  "message": "Budget performance analysis retrieved successfully"
}
```

### 3. Goal Progress Analysis

#### GET /api/analytics/goals/{userId}

**Description**: Get comprehensive goal progress analysis

**Path Parameters**:
- `userId` (Long): User ID

**Response**:
```json
{
  "success": true,
  "data": {
    "goals": [
      {
        "goalId": 1,
        "name": "Vacation Fund",
        "goalType": "SAVINGS",
        "targetAmount": 100000.00,
        "currentAmount": 25000.00,
        "progressPercentage": 25.0,
        "targetDate": "2024-12-31",
        "timeRemaining": "8 months",
        "requiredMonthlySavings": 9375.00,
        "currentMonthlySavings": 5000.00,
        "onTrack": false,
        "status": "ACTIVE",
        "paceAnalysis": {
          "status": "BEHIND",
          "monthsBehind": 2,
          "completionProbability": 65.0
        },
        "accelerationOptions": [
          {
            "strategy": "Increase Monthly Savings",
            "additionalAmount": 4375.00,
            "timeReduction": "2 months",
            "difficulty": "MODERATE"
          }
        ]
      }
    ],
    "overallProgress": {
      "totalGoals": 3,
      "completedGoals": 0,
      "onTrackGoals": 2,
      "behindScheduleGoals": 1,
      "averageProgress": 45.0,
      "totalTargetAmount": 200000.00,
      "totalCurrentAmount": 90000.00
    },
    "insights": [
      "You're ahead of schedule on 2 out of 3 goals",
      "Your vacation fund needs attention to meet the target date",
      "Overall goal progress is 45% complete"
    ],
    "recommendations": [
      {
        "title": "Accelerate Vacation Fund",
        "description": "Increase monthly savings by ₹4,375 to reach your target",
        "impact": "Reach goal 2 months early",
        "priority": "HIGH"
      }
    ]
  },
  "message": "Goal progress analysis retrieved successfully"
}
```

### 4. Income Analysis

#### GET /api/analytics/income/{userId}

**Description**: Get income analysis and trends

**Path Parameters**:
- `userId` (Long): User ID

**Query Parameters**:
- `period` (String, optional): Time period - `month`, `quarter`, `year` (default: `month`)

**Response**:
```json
{
  "success": true,
  "data": {
    "summary": {
      "totalIncome": 50000.00,
      "period": "2024-01",
      "incomeSources": 2,
      "stabilityScore": 85.0
    },
    "incomeBySource": [
      {
        "source": "Salary",
        "amount": 45000.00,
        "percentage": 90.0,
        "trend": "stable"
      },
      {
        "source": "Freelance",
        "amount": 5000.00,
        "percentage": 10.0,
        "trend": "increasing"
      }
    ],
    "trends": {
      "monthOverMonth": 5.0,
      "quarterOverQuarter": 12.0,
      "yearOverYear": 8.0,
      "stability": "high"
    },
    "analysis": {
      "incomeVsExpenses": {
        "ratio": 1.11,
        "status": "healthy"
      },
      "savingsRate": {
        "current": 10.0,
        "recommended": 20.0,
        "status": "below_recommended"
      }
    },
    "insights": [
      "Your income has been stable for 6 months",
      "Freelance income increased 15% this quarter",
      "You're saving 10% of your income, consider increasing to 20%"
    ]
  },
  "message": "Income analysis retrieved successfully"
}
```

---

## LLM-Powered Analytics Endpoints

### 1. AI Insights

#### POST /api/analytics/ai-insights/{userId}

**Description**: Generate AI-powered financial insights

**Path Parameters**:
- `userId` (Long): User ID

**Request Body**:
```json
{
  "analysisType": "spending_patterns",
  "timeframe": "last_3_months",
  "includeRecommendations": true,
  "focusAreas": ["budget", "goals", "savings"],
  "customPrompt": "Optional custom prompt for specific analysis"
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "analysisId": "ai_123456",
    "analysisType": "spending_patterns",
    "insights": "Your spending patterns show a 15% increase in dining out expenses over the last 3 months. This trend, if continued, could impact your savings goals. I notice you're spending 40% more on weekends, which suggests lifestyle inflation.",
    "recommendations": [
      {
        "id": 1,
        "type": "SPENDING_REDUCTION",
        "title": "Optimize Dining Out Expenses",
        "description": "Your dining out expenses have increased significantly. Consider meal prepping and limiting restaurant visits to weekends only.",
        "estimatedSavings": 2000.00,
        "priority": "HIGH",
        "confidenceScore": 0.85,
        "actionItems": [
          "Plan meals for the week on Sundays",
          "Limit dining out to 2 times per week",
          "Use grocery delivery for convenience"
        ],
        "implementationDifficulty": "MODERATE",
        "expectedTimeline": "2-4 weeks"
      }
    ],
    "trends": {
      "spendingIncrease": 15.0,
      "categoryDrift": "dining_out",
      "seasonalPattern": "summer_increase"
    },
    "anomalies": [
      {
        "transactionId": 123,
        "description": "Large dining expense on weekday",
        "reason": "Unusual amount and timing",
        "severity": "MEDIUM"
      }
    ],
    "modelUsed": "gpt-4",
    "confidenceScore": 0.85,
    "processingTime": 1500,
    "tokensUsed": 1250
  },
  "message": "AI insights generated successfully"
}
```

### 2. Conversational Analytics

#### POST /api/analytics/chat/{userId}

**Description**: Answer natural language questions about financial data

**Path Parameters**:
- `userId` (Long): User ID

**Request Body**:
```json
{
  "question": "How much did I spend on food last month?",
  "context": "user is asking about food spending",
  "includeFollowUp": true
}
```

**Response**:
```json
{
  "success": true,
  "data": {
    "answer": "You spent ₹12,000 on food last month, which represents 26.7% of your total spending. This is 15% higher than your average monthly food spending over the past 6 months. Your dining out expenses were ₹8,000 and groceries were ₹4,000.",
    "data": {
      "amount": 12000.00,
      "percentage": 26.7,
      "trend": 15.0,
      "breakdown": {
        "dining_out": 8000.00,
        "groceries": 4000.00
      }
    },
    "insights": [
      "Your food spending is trending upward",
      "Dining out represents 67% of your food expenses",
      "You're above your budget for food this month"
    ],
    "followUpQuestions": [
      "Would you like to see a breakdown by food categories?",
      "Should I analyze your dining out vs. grocery spending patterns?",
      "Would you like suggestions to reduce your food expenses?"
    ],
    "actions": [
      {
        "action": "Review food budget",
        "reason": "You're exceeding your monthly food budget"
      },
      {
        "action": "Analyze dining patterns",
        "reason": "Dining out is your highest food expense"
      }
    ],
    "confidence": 0.92,
    "modelUsed": "gpt-4"
  },
  "message": "Question answered successfully"
}
```

### 3. Personalized Recommendations

#### GET /api/analytics/recommendations/{userId}

**Description**: Get personalized financial recommendations

**Path Parameters**:
- `userId` (Long): User ID

**Query Parameters**:
- `type` (String, optional): Filter by recommendation type - `spending_reduction`, `budget_optimization`, `savings_strategy`, `investment_advice`
- `priority` (String, optional): Filter by priority - `high`, `medium`, `low`

**Response**:
```json
{
  "success": true,
  "data": {
    "recommendations": [
      {
        "id": 1,
        "type": "SPENDING_REDUCTION",
        "title": "Optimize Subscription Services",
        "description": "You have 5 active streaming subscriptions costing ₹1,200/month. Review usage and consider consolidating.",
        "actionItems": [
          "Review Netflix, Amazon Prime, Disney+ usage",
          "Cancel unused subscriptions",
          "Consider family plans for shared services"
        ],
        "estimatedSavings": 600.00,
        "priority": "MEDIUM",
        "confidenceScore": 0.90,
        "implementationDifficulty": "EASY",
        "timeToImplement": "1 week",
        "category": "Entertainment",
        "impact": "Reduce monthly expenses by 5%"
      }
    ],
    "summary": {
      "totalRecommendations": 5,
      "totalPotentialSavings": 3500.00,
      "highPriorityCount": 2,
      "mediumPriorityCount": 2,
      "lowPriorityCount": 1,
      "easyWins": 1200.00,
      "moderateEffort": 1500.00,
      "challenging": 800.00
    },
    "implementationPlan": [
      {
        "phase": "IMMEDIATE",
        "recommendations": [1, 3],
        "expectedSavings": 1200.00,
        "timeline": "1-2 weeks"
      },
      {
        "phase": "SHORT_TERM",
        "recommendations": [2, 4],
        "expectedSavings": 1500.00,
        "timeline": "1-2 months"
      }
    ]
  },
  "message": "Recommendations retrieved successfully"
}
```

### 4. Anomaly Detection

#### GET /api/analytics/anomalies/{userId}

**Description**: Detect unusual transactions and spending patterns

**Path Parameters**:
- `userId` (Long): User ID

**Query Parameters**:
- `days` (Integer, optional): Number of days to analyze (default: 30)
- `severity` (String, optional): Filter by severity - `high`, `medium`, `low`

**Response**:
```json
{
  "success": true,
  "data": {
    "anomalies": [
      {
        "transactionId": 123,
        "amount": 5000.00,
        "description": "Late night transaction at new merchant",
        "category": "Entertainment",
        "type": "AMOUNT",
        "severity": "MEDIUM",
        "confidence": 0.85,
        "reason": "Unusual amount and timing for this category",
        "riskLevel": "UNUSUAL",
        "recommendation": "Verify this transaction was intentional",
        "context": "First transaction with this merchant, unusual time (2:30 AM)"
      }
    ],
    "summary": {
      "totalAnomalies": 3,
      "highSeverityCount": 1,
      "mediumSeverityCount": 2,
      "lowSeverityCount": 0,
      "fraudRiskCount": 0,
      "overallRiskLevel": "LOW"
    },
    "patterns": {
      "unusualTrends": [
        "Increased late-night transactions",
        "New merchant categories"
      ],
      "recommendations": [
        "Review recent transactions for accuracy",
        "Monitor for similar patterns"
      ]
    }
  },
  "message": "Anomaly detection completed successfully"
}
```

---

## Advanced Analytics Endpoints

### 1. Trend Analysis

#### GET /api/analytics/trends/{userId}

**Description**: Get detailed trend analysis over time

**Path Parameters**:
- `userId` (Long): User ID

**Query Parameters**:
- `metric` (String): Analysis metric - `spending`, `income`, `savings`, `budget_adherence`
- `period` (String): Time period - `monthly`, `quarterly`, `yearly`
- `category` (String, optional): Filter by category

**Response**:
```json
{
  "success": true,
  "data": {
    "metric": "spending",
    "period": "monthly",
    "trends": [
      {
        "period": "2024-01",
        "value": 45000.00,
        "change": 12.5,
        "trend": "increasing"
      }
    ],
    "analysis": {
      "overallTrend": "increasing",
      "trendStrength": 0.75,
      "seasonality": "summer_peak",
      "forecast": {
        "nextPeriod": 48000.00,
        "confidence": 0.80
      }
    }
  },
  "message": "Trend analysis completed successfully"
}
```

### 2. Comparative Analysis

#### GET /api/analytics/comparison/{userId}

**Description**: Compare spending across different periods or categories

**Path Parameters**:
- `userId` (Long): User ID

**Query Parameters**:
- `comparisonType` (String): Type of comparison - `period`, `category`, `budget_vs_actual`
- `period1` (String): First period (YYYY-MM)
- `period2` (String): Second period (YYYY-MM)

**Response**:
```json
{
  "success": true,
  "data": {
    "comparisonType": "period",
    "period1": "2024-01",
    "period2": "2023-12",
    "comparison": {
      "totalSpending": {
        "period1": 45000.00,
        "period2": 40000.00,
        "change": 12.5,
        "changeType": "increase"
      },
      "categoryComparison": [
        {
          "category": "Food & Dining",
          "period1": 12000.00,
          "period2": 10000.00,
          "change": 20.0,
          "changeType": "increase"
        }
      ]
    },
    "insights": [
      "Overall spending increased by 12.5%",
      "Food expenses showed the highest increase at 20%"
    ]
  },
  "message": "Comparative analysis completed successfully"
}
```

---

## Error Codes

| Code | Description |
|------|-------------|
| `USER_NOT_FOUND` | User with specified ID not found |
| `INSUFFICIENT_DATA` | Not enough data for analysis |
| `INVALID_PERIOD` | Invalid time period specified |
| `LLM_SERVICE_UNAVAILABLE` | LLM service is not available |
| `ANALYSIS_FAILED` | Analysis processing failed |
| `RATE_LIMIT_EXCEEDED` | API rate limit exceeded |

---

## Rate Limiting

- **Standard Analytics**: 100 requests per minute per user
- **LLM-Powered Analytics**: 10 requests per minute per user
- **Bulk Operations**: 5 requests per minute per user

---

## Caching

- **Standard Analytics**: Cache for 5 minutes
- **LLM-Powered Analytics**: Cache for 1 hour
- **Historical Data**: Cache for 24 hours

---

## Webhooks

### Analytics Webhook Events

#### Budget Alert
```json
{
  "event": "budget_alert",
  "userId": 123,
  "data": {
    "budgetId": 456,
    "category": "Food & Dining",
    "utilization": 95.0,
    "remainingAmount": 400.00,
    "daysRemaining": 5
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Goal Milestone
```json
{
  "event": "goal_milestone",
  "userId": 123,
  "data": {
    "goalId": 789,
    "goalName": "Vacation Fund",
    "milestone": "50%",
    "currentAmount": 50000.00,
    "targetAmount": 100000.00
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

#### Anomaly Detected
```json
{
  "event": "anomaly_detected",
  "userId": 123,
  "data": {
    "transactionId": 101,
    "severity": "HIGH",
    "reason": "Unusual amount and timing",
    "amount": 10000.00
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

This API specification provides a comprehensive foundation for implementing analytics and LLM-powered insights in your expense tracker application. 