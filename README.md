# AI-Powered Expense Tracker

A comprehensive expense tracking application with AI-powered analysis, budget management, and advanced analytics capabilities.

## 🚀 Features

### Phase 1 (Completed ✅)
- **Transaction Management**: Full CRUD operations for expense tracking
- **Category Management**: Flexible categorization system
- **User Management**: Secure user authentication and profiles
- **AI-Powered Analysis**: Intelligent spending insights using OpenAI and Anthropic
- **RESTful API**: Complete REST endpoints for all operations

### Phase 2 (Completed ✅)
- **Budget Management**: Comprehensive budget planning and tracking
- **Financial Goals**: Goal setting and tracking
- **LLM-Powered Analytics**: Unified, natural-language analytics and recommendations
- **Smart Analytics (Strategy-Driven)**: Automatic selection of optimal analytics strategy for large datasets
- **End-to-End Testing**: Automated Python script for full user lifecycle and analytics validation
- **Profile-Aware Security**: Local/test/prod security profiles for safe development and CI

## 📊 Current Project Status

### ✅ Completed Modules
1. **Core Infrastructure** (Phase 1)
2. **Transaction Management** (Phase 1)
3. **Category Management** (Phase 1)
4. **User Management** (Phase 1)
5. **AI Integration** (Phase 1)
6. **Budget Management** (Phase 2)
7. **Financial Goals** (Phase 2)
8. **LLM & Smart Analytics** (Phase 2)

### 🎯 Next Steps
- **Receipt Management**: Receipt upload, OCR processing, expense extraction

## 🖥️ API Endpoints

### Budget Management
```bash
# Create budget
POST /api/budgets?userId=1
{
  "yearMonth": "2025-06",
  "totalBudget": 1000.00,
  "notes": "Monthly budget"
}

# Add category to budget
POST /api/budgets/1/categories
{
  "categoryId": 1,
  "budgetAmount": 500.00,
  "notes": "Food budget"
}

# Get budget status
GET /api/budgets/1/status

# Update spending
PUT /api/budgets/1/spending
{
  "actualSpending": 800.00
}
```

### Financial Goals
```bash
# Create goal
POST /api/goals?userId=1
{
  "name": "Emergency Fund",
  "description": "Save for emergencies",
  "targetAmount": 10000.00,
  "goalType": "SAVINGS",
  "targetDate": "2025-12-31",
  "startDate": "2025-01-01"
}

# Update progress
PUT /api/goals/1/progress
{
  "amount": 5000.00
}

# Get user goals
GET /api/goals/user/1
```

### Transaction Management
```bash
# Get transaction summary
GET /api/transactions/summary?userId=1

# Create transaction
POST /api/transactions
{
  "userId": 1,
  "amount": 50.00,
  "description": "Lunch",
  "categoryId": 1,
  "date": "2025-06-23"
}
```

### User Management
```bash
# Register new user
POST /api/users/register
{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}

# Check email availability
GET /api/users/check-email/user@example.com
```

### LLM & Smart Analytics (Phase 2 - Complete)
```bash
# LLM-Powered Spending Insights
GET /api/llm-analytics/spending-insights/{userId}

# Smart Analytics (Strategy-Driven)
GET /api/smart-analytics/spending-analysis/{userId}
GET /api/smart-analytics/strategy-recommendations/{userId}
GET /api/smart-analytics/strategy-comparison/{userId}
GET /api/smart-analytics/performance-metrics/{userId}

# Conversational Analytics
POST /api/analytics/chat/{userId}
{
  "question": "How much did I spend on food last month?"
}

# Personalized Recommendations
GET /api/analytics/recommendations/{userId}
```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### End-to-End Test Script
- See `test_expense_tracker_e2e.py` for a full user lifecycle and analytics validation.
- **Covers:** Registration, category/budget/goal creation, 300+ transactions, LLM & Smart Analytics endpoints.
- **Run:**
  ```bash
  python3 test_expense_tracker_e2e.py
  ```

## 🔒 Security
- Profile-based security: `local`, `test`, and `prod` profiles for safe development and CI.
- Test profile disables authentication for integration tests.
- Local profile allows all requests for local development.

## 🖼️ Frontend Integration
- **React/Next.js** frontend integrates with all analytics endpoints using REST APIs.
- **Key hooks/components:**
  - `useQuery(['llm-spending-insights', userId], ...)` → `/api/llm-analytics/spending-insights/{userId}`
  - `useQuery(['smart-spending-analysis', userId], ...)` → `/api/smart-analytics/spending-analysis/{userId}`
  - Chat interface → `/api/analytics/chat/{userId}`
  - Recommendations page → `/api/analytics/recommendations/{userId}`
- **Charts:** Pie, line, bar, and progress charts visualize analytics data.
- **WebSocket:** Real-time budget/goal/anomaly notifications.

## 🗂️ Project Structure

```
src/
├── main/java/com/expensetracker/expensetracker/
│   ├── config/          # Security and configuration
│   ├── controller/      # REST API controllers
│   ├── dto/            # Data transfer objects
│   ├── entity/         # JPA entities
│   ├── repository/     # Data access layer
│   └── service/        # Business logic layer
├── test/java/          # Test classes
└── resources/          # Configuration files
```

## 🔧 Configuration

### Environment Variables
- `OPENAI_API_KEY`: OpenAI API key for AI analysis
- `ANTHROPIC_API_KEY`: Anthropic API key for AI analysis
- `SPRING_PROFILES_ACTIVE`: Active Spring profile

### Application Properties
- Database configuration in `application.yml`
- AI service configuration
- Security settings

## 📈 Development Progress

### Phase 1: Foundation ✅
- [x] Core Spring Boot setup
- [x] Database entities and repositories
- [x] Basic CRUD operations
- [x] AI integration
- [x] Security configuration
- [x] Comprehensive testing

### Phase 2: Advanced Features 🔄
- [x] Budget Management (Complete)
- [x] Financial Goals (Complete)
- [x] Advanced Analytics (Not Started)
- [x] Receipt Management (Not Started)

## 🏁 Current Status Summary

### ✅ **What's Working**
- **Application**: Running successfully on http://localhost:8080
- **Authentication**: HTTP Basic auth (admin/admin123)
- **Budget Management**: 11 endpoints, full CRUD, status tracking
- **Financial Goals**: 6 endpoints, progress tracking, status management
- **User Management**: 9 endpoints, registration, profile management
- **Transaction Management**: Full CRUD with summaries
- **Category Management**: Default and user-specific categories
- **AI Integration**: Multi-provider support with fallback
- **Testing**: Comprehensive test coverage

### 🔄 **Next Steps**
1. **Advanced Analytics & Reporting**
   - Spending trend analysis
   - Category breakdown reports
   - Budget vs actual comparisons
   - Export functionality

2. **Receipt Management**
   - Receipt upload and storage
   - OCR processing
   - Receipt-to-transaction linking

### 📊 **Progress Metrics**
- **Phase 2 Completion**: 50% (2 out of 4 modules)
- **API Endpoints**: 17 out of 32 (53%)
- **Entities**: 6 out of 10 (60%)
- **Services**: 3 out of 6 (50%)
- **Controllers**: 2 out of 4 (50%)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🏛️ Support

For questions or issues, please check the documentation or create an issue in the repository. 