# AI-Powered Expense Tracker

A comprehensive expense tracking application with AI-powered analysis and budget management capabilities.

## 🚀 Features

### Phase 1 (Completed ✅)
- **Transaction Management**: Full CRUD operations for expense tracking
- **Category Management**: Flexible categorization system
- **User Management**: Secure user authentication and profiles
- **AI-Powered Analysis**: Intelligent spending insights using OpenAI and Anthropic
- **RESTful API**: Complete REST endpoints for all operations

### Phase 2 (In Progress 🔄)
- **Budget Management**: ✅ **COMPLETED** - Comprehensive budget planning and tracking
  - Monthly budget creation and management
  - Category-based budget allocation
  - Real-time spending tracking and status updates
  - Comprehensive error handling and validation
  - Full test coverage (31 test cases)
  - Production-ready API endpoints
- **Financial Goals**: ❌ **NOT STARTED** - Goal setting and tracking
- **Advanced Analytics**: ❌ **NOT STARTED** - Enhanced reporting and insights
- **Receipt Management**: ❌ **NOT STARTED** - Receipt upload and processing

## 📊 Current Project Status

### ✅ Completed Modules
1. **Core Infrastructure** (Phase 1)
2. **Transaction Management** (Phase 1)
3. **Category Management** (Phase 1)
4. **User Management** (Phase 1)
5. **AI Integration** (Phase 1)
6. **Budget Management** (Phase 2 - Partial)

### 🔄 Phase 2 Remaining Work
1. **Financial Goals Management** - Goal entity, tracking, progress calculation
2. **Advanced Analytics & Reporting** - Spending trends, category reports, budget vs actual
3. **Receipt Management** - Receipt upload, OCR processing, expense extraction

### 🎯 Next Session Starting Point
- **Current Focus**: Financial Goals Management module
- **Next Steps**: Create Goal entity, repository, service, and controller
- **Testing Status**: Budget module fully tested with comprehensive error scenarios

## 🛠 Technology Stack

- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: H2 (in-memory for development)
- **AI Integration**: OpenAI GPT-4, Anthropic Claude
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven
- **Documentation**: OpenAPI/Swagger (planned)

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- OpenAI API key (optional, for AI features)
- Anthropic API key (optional, for AI features)

### Installation
1. Clone the repository
2. Copy `env.example` to `.env` and configure your API keys
3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### API Endpoints

#### Budget Management (Phase 2 - Complete)
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

#### Transaction Management (Phase 1 - Complete)
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

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Specific Test Suites
```bash
# Budget module tests
./mvnw test -Dtest=BudgetServiceTest
./mvnw test -Dtest=BudgetControllerTest

# Transaction module tests
./mvnw test -Dtest=TransactionControllerTest
```

### Test Coverage
- **Budget Module**: 31 test cases (100% coverage)
- **Error Scenarios**: Comprehensive validation and error handling
- **Integration Tests**: Full API endpoint testing

## 📁 Project Structure

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
- [x] Financial Goals (Not Started)
- [x] Advanced Analytics (Not Started)
- [x] Receipt Management (Not Started)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For questions or issues, please check the documentation or create an issue in the repository. 