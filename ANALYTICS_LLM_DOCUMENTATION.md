# Analytics & LLM Integration Documentation

## Table of Contents
1. [Data Model Overview](#data-model-overview)
2. [Available Data Points](#available-data-points)
3. [Analytics Categories](#analytics-categories)
4. [LLM Integration Strategy](#llm-integration-strategy)
5. [API Endpoints Design](#api-endpoints-design)
6. [Frontend Integration Points](#frontend-integration-points)
7. [Implementation Roadmap](#implementation-roadmap)
8. [Large Dataset Handling & Strategy Selection](#large-dataset-handling-and-strategy-selection)
9. [Null-Safety & Category Awareness](#null-safety-and-category-awareness)
10. [Frontend Integration](#frontend-integration)
11. [End-to-End Testing](#end-to-end-testing)

---

## Data Model Overview

### Core Entities
- **User**: Personal info, income, currency, timezone
- **Transaction**: Amount, type (EXPENSE/INCOME/TRANSFER), date, category, description, source
- **Category**: Name, description, icon, color, hierarchy (parent/child)
- **Budget**: Monthly budget with category breakdowns
- **Goal**: Savings, debt payoff, investment targets with progress tracking
- **AIAnalysis**: Stored LLM analysis results
- **Recommendation**: AI-generated actionable suggestions

### Transaction Types
- `EXPENSE`: Money spent
- `INCOME`: Money received  
- `TRANSFER`: Money moved between accounts

### Source Types
- `MANUAL`: User-entered
- `UPI`: UPI transactions
- `BANK`: Bank transfers
- `ACCOUNT_AGGREGATOR`: Third-party aggregation
- `CREDIT_CARD`: Credit card transactions

---

## Available Data Points

### User Profile Data
```json
{
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "monthlyIncome": 50000.00,
  "currency": "INR",
  "timezone": "Asia/Kolkata",
  "dateOfBirth": "1990-01-01"
}
```

### Transaction Data
```json
{
  "id": 1,
  "amount": 1500.00,
  "currency": "INR",
  "transactionType": "EXPENSE",
  "transactionDate": "2024-01-15",
  "transactionTime": "14:30:00",
  "description": "Lunch at Restaurant",
  "category": {
    "id": 1,
    "name": "Food & Dining",
    "color": "#FF6B6B"
  },
  "sourceType": "UPI",
  "bankName": "HDFC",
  "isRecurring": false,
  "tags": "lunch, restaurant",
  "location": "Mumbai, India"
}
```

### Budget Data
```json
{
  "id": 1,
  "yearMonth": "2024-01",
  "totalBudget": 40000.00,
  "actualSpending": 35000.00,
  "status": "UNDER_BUDGET",
  "budgetCategories": [
    {
      "category": "Food & Dining",
      "budgetAmount": 8000.00,
      "actualAmount": 7500.00,
      "budgetPercentage": 20.0,
      "actualPercentage": 21.4
    }
  ]
}
```

### Goal Data
```json
{
  "id": 1,
  "name": "Vacation Fund",
  "targetAmount": 100000.00,
  "currentAmount": 25000.00,
  "goalType": "SAVINGS",
  "targetDate": "2024-12-31",
  "progressPercentage": 25.0,
  "status": "ACTIVE"
}
```

---

## Analytics Categories

### 1. Spending Pattern Analysis

#### Data Points to Calculate
- Total spending by time period (day/week/month/quarter/year)
- Spending by category (absolute and percentage)
- Spending trends over time (growth/decline rates)
- Recurring vs. one-off expenses
- Day-of-week and time-of-day patterns
- Seasonal spending patterns

#### LLM Insights
- "Your dining out expenses have increased 30% this month"
- "You spend 40% more on weekends than weekdays"
- "Your grocery spending is 15% below your 3-month average"

#### Frontend Visualizations
- **Pie Chart**: Category breakdown
- **Line Chart**: Spending trends over time
- **Bar Chart**: Monthly comparison
- **Heatmap**: Day/time spending patterns

### 2. Budget Performance Analysis

#### Data Points to Calculate
- Budget vs. actual spending by category
- Budget utilization percentage
- Over/under budget status
- Projected end-of-month spending
- Burn rate (daily/weekly spending rate)

#### LLM Insights
- "You're 20% under budget this month - great job!"
- "At current pace, you'll exceed your entertainment budget by ₹2,000"
- "Your food budget is 85% used with 10 days remaining"

#### Frontend Visualizations
- **Progress Bars**: Budget utilization by category
- **Gauge Charts**: Overall budget status
- **Forecast Line**: Projected spending vs. budget

### 3. Income Analysis

#### Data Points to Calculate
- Total income by time period
- Income sources breakdown
- Income stability (variance analysis)
- Income vs. expense ratio
- Net savings rate

#### LLM Insights
- "Your income has been stable for 6 months"
- "You're saving 25% of your income, which is excellent"
- "Your freelance income increased 15% this quarter"

#### Frontend Visualizations
- **Waterfall Chart**: Income sources
- **Line Chart**: Income trends
- **Donut Chart**: Income vs. expenses

### 4. Goal Progress Analysis

#### Data Points to Calculate
- Progress percentage for each goal
- Time remaining vs. required pace
- Goal completion probability
- Acceleration opportunities

#### LLM Insights
- "You're ahead of schedule on your vacation goal"
- "To reach your emergency fund goal, save ₹3,000 more per month"
- "At current pace, you'll reach your goal 2 months early"

#### Frontend Visualizations
- **Progress Circles**: Goal completion percentage
- **Timeline**: Goal milestones and projections
- **Bar Chart**: Goal comparison

### 5. Anomaly Detection

#### Data Points to Calculate
- Statistical outliers (amount, frequency, timing)
- Unusual spending patterns
- Duplicate transaction detection
- Geographic anomalies

#### LLM Insights
- "A ₹5,000 transaction at 2am is unusual for you"
- "You've made 3 similar transactions today - possible duplicates?"
- "This is your first transaction in this category this month"

#### Frontend Visualizations
- **Alert Cards**: Anomaly notifications
- **Scatter Plot**: Transaction amount vs. frequency
- **Timeline**: Unusual activity markers

### 6. Savings Opportunities

#### Data Points to Calculate
- High-spending categories with reduction potential
- Recurring expenses analysis
- Subscription optimization opportunities
- Peer comparison (if available)

#### LLM Insights
- "You could save ₹1,200/month by canceling unused subscriptions"
- "Your dining out expenses are 25% higher than similar users"
- "Consider meal prepping to reduce food costs by 20%"

#### Frontend Visualizations
- **Opportunity Cards**: Savings suggestions
- **Comparison Charts**: Peer benchmarks
- **Action Lists**: Specific recommendations

---

## LLM Integration Strategy

### LLM Service Architecture
```java
public interface LLMService {
    String generateAnalysis(String prompt, Map<String, Object> context);
    List<String> generateRecommendations(String userContext);
    Map<String, Object> analyzeSpendingPatterns(List<Map<String, Object>> transactions);
    String categorizeTransaction(String description, List<Category> categories);
    String generateBudgetRecommendation(Double userIncome, Map<String, Double> currentSpending, List<String> goals);
    Map<String, Object> detectAnomalies(List<Transaction> transactions, Map<String, Object> historicalPatterns);
    String generateSavingsOpportunities(Map<String, Double> spendingData, Map<String, Object> userProfile);
}
```

### Prompt Engineering Strategy

#### 1. Spending Pattern Analysis Prompt
```
You are a financial advisor analyzing spending patterns. 
Given the following transaction data for user {userId}:

Transactions: {transactionData}
User Profile: {userProfile}
Budget: {budgetData}
Goals: {goalData}

Analyze the spending patterns and provide insights in JSON format:
{
  "totalSpending": 45000.00,
  "topCategories": ["Food", "Transport", "Entertainment"],
  "insights": "Your dining out expenses have increased 30% this month...",
  "suggestions": ["Consider meal prepping", "Review entertainment budget"],
  "trends": {
    "monthOverMonth": 15.5,
    "seasonalPattern": "Summer spending typically 20% higher"
  }
}
```

#### 2. Budget Recommendation Prompt
```
Based on the following financial data:
- Monthly Income: {income}
- Current Spending: {spendingByCategory}
- Financial Goals: {goals}
- Risk Tolerance: {riskProfile}

Generate a detailed budget recommendation with:
1. Category allocations
2. Savings targets
3. Specific action items
4. Expected outcomes
```

#### 3. Anomaly Detection Prompt
```
Review these recent transactions against historical patterns:
Recent: {recentTransactions}
Historical: {historicalPatterns}

Identify anomalies and explain why they're flagged:
{
  "anomalies": [
    {
      "transactionId": 123,
      "reason": "Unusual amount for this category",
      "confidence": 0.85
    }
  ]
}
```

### Context Building Strategy

#### User Context Object
```json
{
  "userProfile": {
    "age": 30,
    "income": 50000,
    "location": "Mumbai",
    "familySize": 2
  },
  "financialData": {
    "transactions": [...],
    "budgets": [...],
    "goals": [...],
    "categories": [...]
  },
  "historicalPatterns": {
    "averageMonthlySpending": 35000,
    "topCategories": ["Food", "Transport"],
    "savingsRate": 0.25
  },
  "preferences": {
    "riskTolerance": "moderate",
    "savingsGoals": "high",
    "lifestyle": "urban"
  }
}
```

---

## API Endpoints Design

### Analytics Endpoints

#### 1. Spending Analysis
```
GET /api/analytics/spending/{userId}
GET /api/analytics/spending/{userId}?period=month&category=Food
```

Response:
```json
{
  "totalSpending": 45000.00,
  "spendingByCategory": [
    {
      "category": "Food & Dining",
      "amount": 12000.00,
      "percentage": 26.7,
      "trend": 15.5
    }
  ],
  "trends": {
    "monthOverMonth": 12.3,
    "quarterOverQuarter": 8.7
  },
  "insights": "Your dining out expenses have increased 30% this month...",
  "recommendations": ["Consider meal prepping", "Review entertainment budget"]
}
```

#### 2. Budget Performance
```
GET /api/analytics/budget/{userId}/{yearMonth}
```

Response:
```json
{
  "budgetStatus": "UNDER_BUDGET",
  "totalBudget": 40000.00,
  "actualSpending": 35000.00,
  "remainingBudget": 5000.00,
  "utilizationPercentage": 87.5,
  "categoryBreakdown": [
    {
      "category": "Food",
      "budgeted": 8000.00,
      "actual": 7500.00,
      "status": "UNDER_BUDGET",
      "remaining": 500.00
    }
  ],
  "projections": {
    "endOfMonthSpending": 38000.00,
    "willExceedBudget": false
  }
}
```

#### 3. Goal Progress
```
GET /api/analytics/goals/{userId}
```

Response:
```json
{
  "goals": [
    {
      "id": 1,
      "name": "Vacation Fund",
      "targetAmount": 100000.00,
      "currentAmount": 25000.00,
      "progressPercentage": 25.0,
      "timeRemaining": "8 months",
      "requiredMonthlySavings": 9375.00,
      "currentMonthlySavings": 5000.00,
      "onTrack": false
    }
  ],
  "overallProgress": {
    "totalGoals": 3,
    "completedGoals": 0,
    "onTrackGoals": 2,
    "behindScheduleGoals": 1
  }
}
```

#### 4. Anomaly Detection
```
GET /api/analytics/anomalies/{userId}?days=30
```

Response:
```json
{
  "anomalies": [
    {
      "transactionId": 123,
      "amount": 5000.00,
      "description": "Late night transaction",
      "reason": "Unusual time and amount",
      "confidence": 0.85,
      "severity": "MEDIUM"
    }
  ],
  "summary": {
    "totalAnomalies": 3,
    "highSeverity": 1,
    "mediumSeverity": 2
  }
}
```

### LLM-Powered Endpoints

#### 1. AI Insights
```
POST /api/analytics/ai-insights/{userId}
```

Request:
```json
{
  "analysisType": "spending_patterns",
  "timeframe": "last_3_months",
  "includeRecommendations": true
}
```

Response:
```json
{
  "analysisId": "ai_123",
  "insights": "Your spending patterns show a 15% increase in dining out expenses...",
  "recommendations": [
    {
      "title": "Reduce Dining Out",
      "description": "Consider cooking at home 3-4 times per week",
      "estimatedSavings": 2000.00,
      "priority": "HIGH"
    }
  ],
  "confidenceScore": 0.85,
  "modelUsed": "gpt-4",
  "processingTime": 1500
}
```

#### 2. Conversational Analytics
```
POST /api/analytics/chat/{userId}
```

Request:
```json
{
  "question": "How much did I spend on food last month?",
  "context": "user is asking about food spending"
}
```

Response:
```json
{
  "answer": "You spent ₹12,000 on food last month, which is 26.7% of your total spending. This is 15% higher than your average monthly food spending.",
  "data": {
    "amount": 12000.00,
    "percentage": 26.7,
    "trend": 15.0
  },
  "followUpQuestions": [
    "Would you like to see a breakdown by food categories?",
    "Should I analyze your dining out vs. grocery spending?"
  ]
}
```

#### 3. Personalized Recommendations
```
GET /api/analytics/recommendations/{userId}
```

Response:
```json
{
  "recommendations": [
    {
      "id": 1,
      "type": "SPENDING_REDUCTION",
      "title": "Optimize Subscription Services",
      "description": "You have 5 active streaming subscriptions costing ₹1,200/month",
      "actionItems": [
        "Review Netflix, Amazon Prime, Disney+ usage",
        "Cancel unused subscriptions",
        "Consider family plans for shared services"
      ],
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

## Frontend Integration Points

### Data Flow Architecture
```
Frontend → API Gateway → Analytics Service → LLM Service → Database
                ↓
         Cache Layer (Redis)
                ↓
         Real-time Updates (WebSocket)
```

### Chart Library Recommendations
- **Chart.js**: Lightweight, responsive charts
- **D3.js**: Custom, interactive visualizations
- **Recharts**: React-based chart library
- **ApexCharts**: Modern, animated charts

### Real-time Updates
- **WebSocket**: Live budget alerts, anomaly notifications
- **Server-Sent Events**: Real-time spending updates
- **Polling**: Periodic data refresh (every 30 seconds)

### Mobile Considerations
- **Responsive Charts**: Touch-friendly interactions
- **Offline Support**: Cache analytics data locally
- **Push Notifications**: Budget alerts, goal milestones

### Progressive Web App Features
- **Offline Analytics**: Basic charts work without internet
- **Background Sync**: Sync data when connection restored
- **Install Prompt**: Add to home screen for quick access

---

## Implementation Roadmap

### Phase 1: Core Analytics (Week 1-2)
- [ ] Implement basic spending calculations
- [ ] Create budget performance metrics
- [ ] Build goal progress tracking
- [ ] Design analytics DTOs and services

### Phase 2: LLM Integration (Week 3-4)
- [ ] Enhance LLM service with prompt templates
- [ ] Implement AI insights endpoint
- [ ] Add conversational analytics
- [ ] Create recommendation engine

### Phase 3: Advanced Analytics (Week 5-6)
- [ ] Implement anomaly detection
- [ ] Add trend analysis
- [ ] Create savings opportunities analysis
- [ ] Build predictive analytics

### Phase 4: Frontend Integration (Week 7-8)
- [ ] Design chart components
- [ ] Implement real-time updates
- [ ] Add mobile responsiveness
- [ ] Create interactive dashboards

### Phase 5: Optimization (Week 9-10)
- [ ] Performance optimization
- [ ] Caching implementation
- [ ] Error handling improvements
- [ ] User experience enhancements

---

## Large Dataset Handling & Strategy Selection

- The backend uses a `DataStrategyService` to automatically select the optimal analytics strategy:
  - **RAW_DATA**: For small datasets (<100 transactions), sends all data to the LLM for maximum detail.
  - **INTELLIGENT_SUMMARY**: For medium datasets (100-1000), pre-aggregates and summarizes data before LLM analysis.
  - **CHUNKED_PROCESSING**: For large datasets (>1000), processes data in chunks and aggregates insights.
- The `/api/smart-analytics/spending-analysis/{userId}` endpoint returns which strategy was used, along with insights and metadata.

## Null-Safety & Category Awareness

- All analytics endpoints are robust against missing/null data and always provide a category breakdown (never just "Other").
- Category-aware analytics ensure that spending is accurately attributed and visualized.

## Frontend Integration

- The React/Next.js frontend integrates with analytics endpoints using REST APIs and React Query hooks.
- See `FRONTEND_SPECIFICATION.md` for detailed UI integration patterns and data mapping.
- Key endpoints:
  - `/api/llm-analytics/spending-insights/{userId}`
  - `/api/smart-analytics/spending-analysis/{userId}`
  - `/api/analytics/chat/{userId}`
  - `/api/analytics/recommendations/{userId}`

## End-to-End Testing

- The `test_expense_tracker_e2e.py` script validates the full analytics flow, including category-aware insights and large dataset handling.
- Run with:
  ```bash
  python3 test_expense_tracker_e2e.py
  ```

---

## Technical Considerations

### Performance Optimization
- **Database Indexing**: Index on user_id, transaction_date, category_id
- **Caching Strategy**: Redis for frequently accessed analytics
- **Pagination**: Large dataset handling
- **Background Processing**: Async analytics generation

### Security
- **Data Privacy**: User data isolation
- **API Security**: Rate limiting, authentication
- **LLM Security**: Input sanitization, output validation

### Scalability
- **Microservices**: Separate analytics service
- **Load Balancing**: Multiple LLM service instances
- **Database Sharding**: User-based sharding strategy

### Monitoring
- **Analytics**: Track API usage, response times
- **LLM Monitoring**: Token usage, cost tracking
- **Error Tracking**: Comprehensive error logging

---

## Success Metrics

### User Engagement
- Daily active users
- Time spent on analytics dashboard
- Feature adoption rates

### Financial Impact
- User savings rate improvement
- Budget adherence improvement
- Goal completion rates

### Technical Performance
- API response times (< 200ms)
- LLM response accuracy (> 90%)
- System uptime (> 99.9%)

---

This documentation provides a comprehensive foundation for implementing analytics and LLM integration in your expense tracker. Each section can be expanded based on specific requirements and implementation details. 