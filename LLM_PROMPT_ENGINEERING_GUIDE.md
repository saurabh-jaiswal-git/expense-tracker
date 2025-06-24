# LLM Prompt Engineering Guide for Expense Tracker

## Overview
This guide provides specific prompt templates and strategies for generating AI-powered financial insights in the expense tracker application. Each prompt is designed to work with the existing data model and return structured, actionable insights.

---

## Prompt Categories

### 1. Spending Pattern Analysis

#### Basic Spending Analysis Prompt
```
You are a financial advisor analyzing spending patterns. Analyze the following transaction data and provide insights.

User Profile:
- Name: {user.firstName} {user.lastName}
- Age: {calculatedAge}
- Monthly Income: ₹{user.monthlyIncome}
- Location: {user.timezone}
- Currency: {user.currency}

Transaction Data (Last {timeframe}):
{formattedTransactions}

Budget Information:
{formattedBudget}

Financial Goals:
{formattedGoals}

Please provide analysis in the following JSON format:
{
  "totalSpending": <total_amount>,
  "spendingByCategory": [
    {
      "category": "<category_name>",
      "amount": <amount>,
      "percentage": <percentage>,
      "trend": <percentage_change_from_previous_period>
    }
  ],
  "topSpendingCategories": ["<category1>", "<category2>", "<category3>"],
  "insights": [
    "<insight1>",
    "<insight2>",
    "<insight3>"
  ],
  "trends": {
    "monthOverMonth": <percentage_change>,
    "quarterOverQuarter": <percentage_change>,
    "seasonalPattern": "<description>"
  },
  "suggestions": [
    {
      "title": "<suggestion_title>",
      "description": "<detailed_description>",
      "estimatedSavings": <amount>,
      "priority": "HIGH|MEDIUM|LOW"
    }
  ],
  "anomalies": [
    {
      "transactionId": <id>,
      "reason": "<why_it's_anomalous>",
      "severity": "HIGH|MEDIUM|LOW"
    }
  ]
}

Focus on:
1. Identifying spending patterns and trends
2. Highlighting unusual transactions
3. Providing actionable savings suggestions
4. Comparing against budget and goals
5. Using natural, conversational language
```

#### Advanced Spending Analysis with Context
```
You are an AI financial advisor with expertise in personal finance. You're analyzing spending patterns for {user.firstName}, a {age}-year-old professional living in {location}.

Financial Context:
- Monthly Income: ₹{monthlyIncome}
- Current Month Spending: ₹{currentMonthSpending}
- Previous Month Spending: ₹{previousMonthSpending}
- Savings Rate: {savingsRate}%
- Active Goals: {activeGoalsCount}

Recent Transactions (Last 30 days):
{recentTransactions}

Historical Patterns (Last 6 months):
- Average Monthly Spending: ₹{averageSpending}
- Top Categories: {topCategories}
- Seasonal Trends: {seasonalTrends}

Budget Status:
{budgetStatus}

Please provide a comprehensive analysis that includes:

1. **Spending Overview**: Total spending, category breakdown, trends
2. **Pattern Recognition**: Recurring expenses, unusual transactions, spending habits
3. **Budget Analysis**: Performance against budget, areas of concern
4. **Goal Impact**: How current spending affects financial goals
5. **Actionable Recommendations**: Specific steps to improve financial health

Format your response as JSON with the following structure:
{
  "summary": {
    "totalSpending": <amount>,
    "spendingChange": <percentage>,
    "budgetStatus": "UNDER_BUDGET|OVER_BUDGET|ON_TRACK",
    "savingsRate": <percentage>
  },
  "categoryAnalysis": [
    {
      "category": "<name>",
      "amount": <amount>,
      "percentage": <percentage>,
      "trend": "<increasing|decreasing|stable>",
      "budgetStatus": "<under|over|on_track>",
      "insight": "<specific_insight>"
    }
  ],
  "keyInsights": [
    "<insight1>",
    "<insight2>",
    "<insight3>"
  ],
  "recommendations": [
    {
      "type": "SPENDING_REDUCTION|BUDGET_ADJUSTMENT|SAVINGS_STRATEGY",
      "title": "<title>",
      "description": "<detailed_description>",
      "estimatedImpact": "<amount_or_percentage>",
      "priority": "HIGH|MEDIUM|LOW",
      "actionItems": ["<step1>", "<step2>", "<step3>"]
    }
  ],
  "anomalies": [
    {
      "transactionId": <id>,
      "description": "<transaction_description>",
      "reason": "<why_anomalous>",
      "severity": "HIGH|MEDIUM|LOW",
      "suggestion": "<what_to_do>"
    }
  ]
}
```

### 2. Budget Recommendation Generation

#### Comprehensive Budget Planning Prompt
```
You are a certified financial planner creating a personalized budget for {user.firstName}.

User Profile:
- Age: {age}
- Monthly Income: ₹{monthlyIncome}
- Location: {location}
- Family Size: {familySize}
- Risk Tolerance: {riskTolerance}

Current Financial Situation:
- Current Monthly Spending: ₹{currentSpending}
- Current Savings Rate: {currentSavingsRate}%
- Emergency Fund: ₹{emergencyFund}
- Debt: ₹{totalDebt}

Financial Goals:
{formattedGoals}

Current Spending by Category:
{currentSpendingByCategory}

Please create a comprehensive budget recommendation that includes:

1. **50/30/20 Rule Application**: Needs (50%), Wants (30%), Savings (20%)
2. **Category-Specific Allocations**: Detailed breakdown by expense category
3. **Savings Strategy**: Emergency fund, goal-specific savings, investments
4. **Debt Management**: If applicable, debt payoff strategy
5. **Lifestyle Adjustments**: Specific changes to achieve budget targets

Return your response in this JSON format:
{
  "budgetSummary": {
    "totalIncome": <amount>,
    "needsAllocation": <amount>,
    "wantsAllocation": <amount>,
    "savingsAllocation": <amount>,
    "targetSavingsRate": <percentage>
  },
  "categoryAllocations": [
    {
      "category": "<category_name>",
      "currentAmount": <current_amount>,
      "recommendedAmount": <recommended_amount>,
      "percentage": <percentage_of_income>,
      "priority": "ESSENTIAL|IMPORTANT|NICE_TO_HAVE",
      "adjustment": "<increase|decrease|maintain>",
      "reasoning": "<why_this_allocation>"
    }
  ],
  "savingsStrategy": {
    "emergencyFund": {
      "targetAmount": <amount>,
      "monthlyContribution": <amount>,
      "timeToTarget": "<months>"
    },
    "goalSavings": [
      {
        "goalName": "<goal_name>",
        "monthlyContribution": <amount>,
        "priority": "HIGH|MEDIUM|LOW"
      }
    ],
    "investmentRecommendations": [
      {
        "type": "<investment_type>",
        "amount": <amount>,
        "rationale": "<why_recommended>"
      }
    ]
  },
  "actionPlan": [
    {
      "step": 1,
      "action": "<specific_action>",
      "timeline": "<when_to_do>",
      "expectedImpact": "<what_it_will_achieve>"
    }
  ],
  "riskFactors": [
    "<potential_risk1>",
    "<potential_risk2>"
  ],
  "successMetrics": {
    "monthlySavingsTarget": <amount>,
    "budgetAdherenceTarget": <percentage>,
    "goalProgressTarget": <percentage>
  }
}
```

### 3. Anomaly Detection

#### Transaction Anomaly Detection Prompt
```
You are a financial security expert analyzing transaction patterns for potential anomalies.

User Profile:
- Typical Spending Pattern: {typicalPattern}
- Average Transaction Amount: ₹{averageAmount}
- Common Categories: {commonCategories}
- Typical Transaction Times: {typicalTimes}

Recent Transactions (Last 7 days):
{recentTransactions}

Historical Baseline (Last 3 months):
- Average Daily Spending: ₹{averageDaily}
- Most Frequent Categories: {frequentCategories}
- Typical Transaction Frequency: {frequency}
- Geographic Patterns: {geographicPatterns}

Please analyze the recent transactions for anomalies based on:
1. **Amount Anomalies**: Transactions significantly above/below typical amounts
2. **Timing Anomalies**: Unusual transaction times or frequencies
3. **Category Anomalies**: First-time or rare category usage
4. **Geographic Anomalies**: Transactions from unusual locations
5. **Pattern Anomalies**: Deviations from established spending patterns

Return analysis in JSON format:
{
  "anomalies": [
    {
      "transactionId": <id>,
      "type": "AMOUNT|TIMING|CATEGORY|GEOGRAPHIC|PATTERN",
      "severity": "HIGH|MEDIUM|LOW",
      "confidence": <0.0_to_1.0>,
      "description": "<what_makes_it_anomalous>",
      "riskLevel": "FRAUD|UNUSUAL|NOTABLE",
      "recommendation": "<what_user_should_do>",
      "context": "<additional_context>"
    }
  ],
  "summary": {
    "totalAnomalies": <count>,
    "highSeverityCount": <count>,
    "fraudRiskCount": <count>,
    "overallRiskLevel": "LOW|MEDIUM|HIGH"
  },
  "patterns": {
    "unusualTrends": ["<trend1>", "<trend2>"],
    "recommendations": ["<rec1>", "<rec2>"]
  }
}
```

### 4. Goal Progress Analysis

#### Goal Progress and Acceleration Prompt
```
You are a financial coach analyzing progress toward financial goals for {user.firstName}.

Current Goals:
{formattedGoals}

Financial Context:
- Monthly Income: ₹{monthlyIncome}
- Current Monthly Savings: ₹{currentSavings}
- Average Monthly Spending: ₹{averageSpending}
- Emergency Fund: ₹{emergencyFund}

Recent Goal Contributions:
{recentContributions}

Please analyze each goal and provide:

1. **Progress Assessment**: Current status vs. target timeline
2. **Pace Analysis**: Whether on track, ahead, or behind schedule
3. **Acceleration Opportunities**: Ways to reach goals faster
4. **Risk Assessment**: Potential obstacles to goal achievement
5. **Recommendations**: Specific actions to improve progress

Return analysis in JSON format:
{
  "goals": [
    {
      "goalId": <id>,
      "name": "<goal_name>",
      "currentStatus": {
        "currentAmount": <amount>,
        "targetAmount": <amount>,
        "progressPercentage": <percentage>,
        "timeRemaining": "<months>",
        "requiredMonthlySavings": <amount>,
        "currentMonthlySavings": <amount>
      },
      "paceAnalysis": {
        "status": "ON_TRACK|AHEAD|BEHIND",
        "monthsAhead": <number>,
        "monthsBehind": <number>,
        "completionProbability": <percentage>
      },
      "accelerationOptions": [
        {
          "strategy": "<strategy_name>",
          "additionalMonthlySavings": <amount>,
          "timeReduction": "<months>",
          "difficulty": "EASY|MODERATE|CHALLENGING",
          "description": "<how_to_implement>"
        }
      ],
      "risks": [
        {
          "risk": "<risk_description>",
          "probability": "LOW|MEDIUM|HIGH",
          "mitigation": "<how_to_reduce_risk>"
        }
      ],
      "recommendations": [
        {
          "action": "<specific_action>",
          "impact": "<expected_impact>",
          "priority": "HIGH|MEDIUM|LOW",
          "timeline": "<when_to_do>"
        }
      ]
    }
  ],
  "overallProgress": {
    "totalGoals": <count>,
    "onTrackGoals": <count>,
    "aheadOfScheduleGoals": <count>,
    "behindScheduleGoals": <count>,
    "averageProgress": <percentage>
  },
  "strategicRecommendations": [
    {
      "type": "SAVINGS_INCREASE|GOAL_PRIORITIZATION|TIMELINE_ADJUSTMENT",
      "title": "<title>",
      "description": "<description>",
      "impact": "<expected_impact>",
      "implementation": ["<step1>", "<step2>"]
    }
  ]
}
```

### 5. Conversational Analytics

#### Natural Language Query Processing Prompt
```
You are a helpful financial assistant for {user.firstName}. The user is asking questions about their financial data in natural language.

User Question: "{userQuestion}"

Available Financial Data:
- Transactions: {transactionData}
- Budgets: {budgetData}
- Goals: {goalData}
- Categories: {categoryData}

Please provide a helpful, conversational response that:
1. Answers the user's question directly
2. Provides relevant context and insights
3. Suggests follow-up questions or actions
4. Uses natural, friendly language
5. Includes specific numbers and percentages when relevant

Format your response as JSON:
{
  "answer": "<direct_answer_to_question>",
  "data": {
    "relevantNumbers": {
      "amount": <amount>,
      "percentage": <percentage>,
      "trend": "<increasing|decreasing|stable>"
    },
    "context": "<additional_context>"
  },
  "insights": [
    "<insight1>",
    "<insight2>"
  ],
  "followUpQuestions": [
    "<suggested_question1>",
    "<suggested_question2>"
  ],
  "actions": [
    {
      "action": "<suggested_action>",
      "reason": "<why_suggested>"
    }
  ],
  "confidence": <0.0_to_1.0>
}
```

### 6. Savings Opportunities Analysis

#### Comprehensive Savings Analysis Prompt
```
You are a financial optimization expert analyzing savings opportunities for {user.firstName}.

Current Financial Situation:
- Monthly Income: ₹{monthlyIncome}
- Monthly Spending: ₹{monthlySpending}
- Current Savings Rate: {savingsRate}%

Spending Analysis:
{spendingAnalysis}

Recurring Expenses:
{recurringExpenses}

Subscription Services:
{subscriptions}

Please identify potential savings opportunities across:

1. **Recurring Expenses**: Subscriptions, memberships, services
2. **High-Spending Categories**: Areas with reduction potential
3. **Lifestyle Adjustments**: Changes that could reduce spending
4. **Efficiency Improvements**: Ways to get more value for money
5. **Alternative Options**: Cheaper alternatives to current expenses

Return analysis in JSON format:
{
  "savingsOpportunities": [
    {
      "category": "<category>",
      "type": "SUBSCRIPTION|LIFESTYLE|EFFICIENCY|ALTERNATIVE",
      "title": "<opportunity_title>",
      "description": "<detailed_description>",
      "currentCost": <amount>,
      "potentialSavings": <amount>,
      "implementationDifficulty": "EASY|MODERATE|CHALLENGING",
      "timeToImplement": "<timeframe>",
      "riskLevel": "LOW|MEDIUM|HIGH",
      "actionSteps": ["<step1>", "<step2>", "<step3>"],
      "expectedImpact": "<what_will_happen>"
    }
  ],
  "summary": {
    "totalPotentialSavings": <amount>,
    "easyWins": <amount>,
    "moderateEffort": <amount>,
    "challenging": <amount>,
    "recommendedPriority": ["<opportunity1>", "<opportunity2>"]
  },
  "implementationPlan": [
    {
      "phase": "IMMEDIATE|SHORT_TERM|LONG_TERM",
      "opportunities": ["<opportunity1>", "<opportunity2>"],
      "expectedSavings": <amount>,
      "timeline": "<timeframe>"
    }
  ],
  "riskConsiderations": [
    {
      "risk": "<potential_risk>",
      "mitigation": "<how_to_address>"
    }
  ]
}
```

---

## Prompt Optimization Strategies

### 1. Context Window Management
- **Prioritize Recent Data**: Focus on last 3-6 months for most analyses
- **Summarize Historical Data**: Use aggregated statistics for older data
- **Chunk Large Datasets**: Break down large transaction sets into manageable chunks

### 2. Response Formatting
- **Structured JSON**: Always request structured responses for consistency
- **Error Handling**: Include fallback responses for parsing failures
- **Validation**: Specify expected data types and ranges

### 3. Personalization
- **User Context**: Include age, location, income level for relevant advice
- **Financial Goals**: Reference specific goals in recommendations
- **Risk Tolerance**: Adjust suggestions based on user's risk profile

### 4. Safety and Ethics
- **Data Privacy**: Never include sensitive personal information
- **Conservative Estimates**: Use conservative estimates for financial projections
- **Disclaimers**: Include appropriate disclaimers for financial advice

---

## Implementation Notes

### Prompt Variables
Replace placeholders like `{user.firstName}` with actual data from your entities:
- `{user.firstName}` → `user.getFirstName()`
- `{monthlyIncome}` → `user.getMonthlyIncome().toString()`
- `{formattedTransactions}` → JSON string of transaction data

### Response Parsing
Always implement robust JSON parsing with fallbacks:
```java
try {
    return objectMapper.readValue(llmResponse, AnalysisResponse.class);
} catch (Exception e) {
    logger.error("Failed to parse LLM response", e);
    return createFallbackResponse();
}
```

### Caching Strategy
Cache LLM responses for similar queries to reduce API costs and improve performance.

### Error Handling
Implement comprehensive error handling for:
- API failures
- Invalid responses
- Rate limiting
- Token limit exceeded

This prompt engineering guide provides a solid foundation for implementing AI-powered financial insights in your expense tracker application. 