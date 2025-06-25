# Analytics API Specification

## Overview
This document defines the REST API endpoints for analytics and LLM-powered insights in the expense tracker application. All endpoints follow RESTful conventions and return JSON responses.

## Base URL
```
http://localhost:8080/api
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

## LLM & Smart Analytics Endpoints

### 1. LLM-Powered Spending Insights

#### GET /api/llm-analytics/spending-insights/{userId}

**Description**: Get unified LLM-powered spending insights and category breakdown for a user.

**Path Parameters:**
- `userId` (Long): User ID

**Query Parameters:**
- `start` (String, optional): Start date (YYYY-MM-DD)
- `end` (String, optional): End date (YYYY-MM-DD)

**Response:**
```json
{
  "llmText": "You spent 40% of your income on food. Consider cooking at home more often.",
  "visualData": {
    "categoryBreakdown": { "Food": 400, "Transport": 200 },
    "totalSpent": 600.0,
    "transactionCount": 10
  }
}
```

### 2. Smart Analytics (Strategy-Driven)

#### GET /api/smart-analytics/spending-analysis/{userId}

**Description**: Get smart spending analysis with automatic strategy selection based on dataset size.

**Path Parameters:**
- `userId` (Long): User ID

**Query Parameters:**
- `startDate` (String, optional): Start date (YYYY-MM-DD)
- `endDate` (String, optional): End date (YYYY-MM-DD)

**Response:**
```json
{
  "userId": 1,
  "insights": "Based on your spending analysis...",
  "strategy": "INTELLIGENT_SUMMARY",
  "transactionCount": 250,
  "metadata": {
    "strategy": "INTELLIGENT_SUMMARY",
    "transactionCount": 250,
    "performanceMetrics": {
      "strategySelectionTime": "~0.6ms",
      "countQueryTime": "~0.5ms"
    }
  },
  "analysisType": "smart_automated"
}
```

#### GET /api/smart-analytics/strategy-recommendations/{userId}
**Description**: Get recommended analytics strategy and reasoning for a user.

#### GET /api/smart-analytics/strategy-comparison/{userId}
**Description**: Compare all analytics strategies for a user and dataset.

#### GET /api/smart-analytics/performance-metrics/{userId}
**Description**: Get performance metrics for analytics strategy selection.

### 3. Conversational Analytics

#### POST /api/analytics/chat/{userId}
**Description**: Ask natural language questions about financial data.

**Request Body:**
```json
{
  "question": "How much did I spend on food last month?"
}
```
**Response:**
```json
{
  "answer": "You spent ₹12,000 on food last month, which represents 26.7% of your total spending.",
  "data": {
    "amount": 12000.00,
    "percentage": 26.7,
    "trend": 15.0
  },
  "insights": ["Your food spending is trending upward"],
  "followUpQuestions": ["Would you like to see a breakdown by food categories?"],
  "actions": [
    { "action": "Review food budget", "reason": "You're exceeding your monthly food budget" }
  ],
  "confidence": 0.92,
  "modelUsed": "gpt-4"
}
```

### 4. Personalized Recommendations

#### GET /api/analytics/recommendations/{userId}
**Description**: Get personalized financial recommendations.

**Response:**
```json
{
  "recommendations": [
    {
      "id": 1,
      "type": "SPENDING_REDUCTION",
      "title": "Optimize Subscription Services",
      "description": "You have 5 active streaming subscriptions costing ₹1,200/month.",
      "actionItems": ["Review Netflix, Amazon Prime, Disney+ usage"],
      "estimatedSavings": 600.00,
      "priority": "MEDIUM",
      "confidenceScore": 0.90
    }
  ],
  "summary": {
    "totalRecommendations": 5,
    "totalPotentialSavings": 3500.00,
    "highPriorityCount": 2
  }
}
```

---

## Legacy Analytics Endpoints

> **Note:** All legacy analytics endpoints and DTOs (e.g., `/api/analytics/spending/{userId}`) have been removed. Use the LLM and Smart Analytics endpoints above.

---

## Frontend Integration

- The React/Next.js frontend integrates with these endpoints using REST APIs and React Query hooks.
- See `FRONTEND_SPECIFICATION.md` for detailed UI integration patterns and data mapping.

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