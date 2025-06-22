# AI-Powered Expense Tracker

A comprehensive expense tracking application with AI-driven financial insights using OpenAI GPT and Anthropic Claude models.

## 🚀 Project Overview

This is a Spring Boot application that provides AI-powered financial analysis and expense tracking. The application integrates with multiple AI providers (OpenAI and Anthropic) to analyze spending patterns and provide personalized financial insights.

## 🏗️ System Architecture

### Technology Stack
- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: H2 (development), PostgreSQL (production)
- **Security**: Spring Security with Basic Authentication
- **ORM**: Spring Data JPA with Hibernate
- **AI Integration**: OpenAI GPT-3.5-turbo & Anthropic Claude-3-Haiku
- **Build Tool**: Maven

### Database Schema
The application uses a comprehensive database design supporting:
- **Core Tables**: users, categories, transactions, user_categories
- **AI Analysis**: ai_analysis, recommendations
- **Features**: Manual entry, AI-powered spending analysis, financial insights

## 📁 Current Project Structure

```
expense-tracker/
├── src/main/java/com/expensetracker/expensetracker/
│   ├── config/
│   │   └── SecurityConfig.java              # Security configuration
│   ├── controller/
│   │   ├── AIAnalysisController.java        # AI analysis endpoints
│   │   ├── CategoryController.java          # Category management
│   │   ├── HealthController.java            # Health check endpoints
│   │   ├── TransactionController.java       # Transaction CRUD operations
│   │   └── UserController.java              # User management
│   ├── entity/
│   │   ├── AIAnalysis.java                  # AI analysis results
│   │   ├── Category.java                    # Expense categories
│   │   ├── Recommendation.java              # AI recommendations
│   │   ├── Transaction.java                 # Financial transactions
│   │   ├── User.java                        # User management
│   │   └── UserCategory.java                # User-specific categories
│   ├── repository/
│   │   ├── AIAnalysisRepository.java        # AI analysis data access
│   │   ├── CategoryRepository.java          # Category data access
│   │   ├── TransactionRepository.java       # Transaction data access
│   │   ├── UserCategoryRepository.java      # User category data access
│   │   └── UserRepository.java              # User data access
│   ├── service/
│   │   ├── AIAnalysisService.java           # AI analysis business logic
│   │   ├── AnthropicLLMService.java         # Anthropic Claude integration
│   │   ├── LLMService.java                  # LLM service interface
│   │   └── OpenAILLMService.java            # OpenAI GPT integration
│   └── ExpenseTrackerApplication.java       # Main application class
├── src/main/resources/
│   ├── application.yml                      # Application configuration
│   ├── data.sql                            # Sample data
│   └── static/                             # Static resources
└── pom.xml                                 # Maven dependencies
```

## ✅ What's Currently Implemented

### 1. Core Infrastructure
- ✅ Spring Boot 3.5.3 application with Java 17
- ✅ H2 in-memory database configuration
- ✅ JPA entities for core tables (User, Category, Transaction, UserCategory, AIAnalysis, Recommendation)
- ✅ Basic authentication (admin/admin)
- ✅ Health check endpoints (`/actuator/health`)
- ✅ Database schema auto-generation by Hibernate
- ✅ Sample data loading

### 2. Transaction Management (CRUD Operations)
- ✅ **TransactionController**: Full CRUD operations for transactions
- ✅ **TransactionRepository**: Data access layer with custom queries
- ✅ **Transaction Summary**: Spending analytics and summaries
- ✅ **Pagination Support**: Efficient data retrieval with pagination
- ✅ **Date Range Filtering**: Filter transactions by date ranges
- ✅ **User-Specific Queries**: Secure user data isolation

### 3. Category Management
- ✅ **CategoryController**: CRUD operations for expense categories
- ✅ **CategoryRepository**: Data access for categories
- ✅ **UserCategoryRepository**: User-specific custom categories
- ✅ **Default Categories**: Pre-defined expense categories
- ✅ **Custom Categories**: User-created categories

### 4. User Management
- ✅ **UserController**: User CRUD operations and profile management
- ✅ **UserRepository**: User data access layer
- ✅ **Profile Management**: User profile summaries and updates
- ✅ **Security Integration**: Role-based access control

### 5. AI Integration
- ✅ **Multi-Provider Support**: OpenAI and Anthropic integration
- ✅ **Configurable Provider**: Switch between AI providers via configuration
- ✅ **Spending Analysis**: AI-powered transaction analysis and insights
- ✅ **Recommendations**: Personalized financial advice and suggestions
- ✅ **Fallback Mechanism**: Graceful handling of API failures

### 6. Security
- ✅ Basic authentication configured
- ✅ Protected endpoints requiring authentication
- ✅ Input validation and sanitization
- ✅ CSRF protection (configurable for testing)

## 🔄 Application Status

**Current State**: ✅ **FULLY FUNCTIONAL WITH COMPLETE CRUD OPERATIONS**

The application is running successfully with all core features implemented:
- ✅ Transaction management (Create, Read, Update, Delete)
- ✅ Category management with user-specific categories
- ✅ User management and profile features
- ✅ AI-powered spending analysis
- ✅ Comprehensive REST API endpoints
- ✅ Database persistence with H2
- ✅ Authentication and security

## 🎯 Planned Features (Phased Approach)

### Phase 1: Manual Entry ✅ **COMPLETED**
- ✅ Basic CRUD operations for transactions
- ✅ Category management
- ✅ User authentication
- ✅ Transaction controllers and services
- ✅ Repository layer implementation
- ✅ REST API endpoints

### Phase 2: Enhanced Features (NEXT PRIORITY)
- 📋 Budget tracking and alerts
- 📋 Financial goals management
- 📋 Expense analytics and reporting
- 📋 Receipt upload and storage
- 📋 Advanced spending insights

### Phase 3: Bank Integration
- 📋 UPI transaction integration
- 📋 Account Aggregator framework
- 📋 Credit card statement parsing
- 📋 Automated transaction categorization

### Phase 4: Advanced AI Features
- 📋 Transaction categorization using AI
- 📋 Spending pattern analysis
- 📋 Financial advice and recommendations
- 📋 Anomaly detection

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- OpenAI API key OR Anthropic API key

### 1. Clone the Repository
```bash
git clone <your-repo-url>
cd expense-tracker
```

### 2. Set Up Environment Variables
Copy the example environment file and configure your API keys:

```bash
cp env.example .env
```

Edit `.env` and set your API keys:
```bash
# Choose your AI provider: 'openai' or 'anthropic'
AI_PROVIDER=anthropic

# For OpenAI
OPENAI_API_KEY=your_openai_api_key_here

# For Anthropic
ANTHROPIC_API_KEY=your_anthropic_api_key_here

# Optional: Customize admin credentials
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin
```

### 3. Run the Application
```bash
# Set Java 17 path (if needed on macOS)
export JAVA_HOME="$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

# Run the application
./mvnw spring-boot:run
```

### 4. Access the Application
- **Application**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **AI Analysis Endpoint**: POST http://localhost:8080/api/ai/analyze

### 5. Test AI Integration
```bash
# Test with curl (replace admin:admin with your credentials)
curl -u admin:admin -X POST http://localhost:8080/api/ai/analyze \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "transactions": [
      {"description": "Grocery shopping", "amount": 150.00, "category": "Food"},
      {"description": "Gas station", "amount": 45.00, "category": "Transportation"},
      {"description": "Netflix subscription", "amount": 15.00, "category": "Entertainment"}
    ]
  }'
```

## 🔧 Configuration

### AI Provider Configuration
The application supports multiple AI providers. Configure in `application.yml`:

```yaml
ai:
  provider: anthropic  # or 'openai'
  openai:
    model: gpt-3.5-turbo
    temperature: 0.7
  anthropic:
    model: claude-3-haiku-20240307
    temperature: 0.7
```

### Environment Variables
- `AI_PROVIDER`: Set to 'openai' or 'anthropic'
- `OPENAI_API_KEY`: Your OpenAI API key
- `ANTHROPIC_API_KEY`: Your Anthropic API key
- `ADMIN_USERNAME`: Admin username (default: admin)
- `ADMIN_PASSWORD`: Admin password (default: admin)

## 📊 API Endpoints

### Health & Status
- `GET /actuator/health` - Application health status

### Transaction Management
- `POST /api/transactions` - Create a new transaction
- `GET /api/transactions/{id}` - Get transaction by ID
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction
- `GET /api/transactions/user/{userId}` - Get user's transactions (paginated)
- `GET /api/transactions/user/{userId}/summary` - Get transaction summary

### Category Management
- `GET /api/categories` - Get all categories
- `GET /api/categories/default` - Get default categories
- `POST /api/categories` - Create new category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category
- `GET /api/categories/user/{userId}` - Get user's categories

### User Management
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/{userId}/profile` - Get user profile summary

### AI Analysis
- `POST /api/ai/analyze` - Analyze spending patterns with AI

## 🔒 Security Features

- **Authentication**: Basic authentication with configurable credentials
- **Input Validation**: Comprehensive validation for all API inputs
- **CSRF Protection**: Enabled by default (can be disabled for testing)
- **Secure Headers**: HSTS, content type options, frame options
- **Logging**: Sensitive data masking in logs

## 🚨 Security Notes

⚠️ **Important**: Before deploying to production:
1. Change default admin credentials
2. Enable CSRF protection
3. Use a production database (PostgreSQL/MySQL)
4. Set up proper SSL/TLS
5. Configure rate limiting
6. Review and update security configurations

## 📋 Development

### Project Structure
```
expense-tracker/
├── src/main/java/com/expensetracker/expensetracker/
│   ├── config/
│   │   └── SecurityConfig.java              # Security configuration
│   ├── controller/
│   │   ├── AIAnalysisController.java        # AI analysis endpoints
│   │   ├── CategoryController.java          # Category management
│   │   ├── HealthController.java            # Health check endpoints
│   │   ├── TransactionController.java       # Transaction CRUD operations
│   │   └── UserController.java              # User management
│   ├── entity/
│   │   ├── AIAnalysis.java                  # AI analysis results
│   │   ├── Category.java                    # Expense categories
│   │   ├── Recommendation.java              # AI recommendations
│   │   ├── Transaction.java                 # Financial transactions
│   │   ├── User.java                        # User management
│   │   └── UserCategory.java                # User-specific categories
│   ├── repository/
│   │   ├── AIAnalysisRepository.java        # AI analysis data access
│   │   ├── CategoryRepository.java          # Category data access
│   │   ├── TransactionRepository.java       # Transaction data access
│   │   ├── UserCategoryRepository.java      # User category data access
│   │   └── UserRepository.java              # User data access
│   ├── service/
│   │   ├── AIAnalysisService.java           # AI analysis business logic
│   │   ├── AnthropicLLMService.java         # Anthropic Claude integration
│   │   ├── LLMService.java                  # LLM service interface
│   │   └── OpenAILLMService.java            # OpenAI GPT integration
│   └── ExpenseTrackerApplication.java       # Main application class
├── src/main/resources/
│   ├── application.yml                      # Application configuration
│   └── data.sql                            # Sample data
├── env.example                              # Environment variables template
├── SECURITY.md                              # Security documentation
├── PRODUCTION_DEPLOYMENT.md                 # Production deployment guide
└── pom.xml                                 # Maven dependencies
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For issues and questions:
1. Check the documentation in `SECURITY.md` and `PRODUCTION_DEPLOYMENT.md`
2. Review the troubleshooting section below
3. Create an issue in the repository

## 🔧 Troubleshooting

### Common Issues

1. **Java Version Error**: Ensure you're using Java 17 or higher
   ```bash
   java -version
   ```

2. **API Key Issues**: Verify your API keys are set correctly
   ```bash
   echo $OPENAI_API_KEY
   echo $ANTHROPIC_API_KEY
   ```

3. **Build Failures**: Clean and rebuild
   ```bash
   ./mvnw clean package
   ```

4. **Authentication Issues**: Check credentials in environment variables or use defaults (admin/admin)

### Getting Help
- Check the application logs for detailed error messages
- Verify your API provider has sufficient credits/quota
- Ensure all environment variables are set correctly

## 🏛️ System Design Document

### Database Schema Details
- **Users**: Complete user profile with financial preferences
- **Categories**: Hierarchical expense categorization
- **Transactions**: Comprehensive transaction tracking with source integration
- **UserCategories**: Personalized category management

### Backend Architecture
- **Controller Layer**: REST API endpoints
- **Service Layer**: Business logic and validation
- **Repository Layer**: Data access with Spring Data JPA
- **Entity Layer**: JPA entities with auditing

### Security Architecture
- **Authentication**: Spring Security with Basic Auth (to be enhanced)
- **Authorization**: Role-based access control
- **Data Protection**: Encrypted sensitive data

### AI Integration Plan
- **Transaction Categorization**: ML models for automatic categorization
- **Spending Analysis**: Pattern recognition and insights
- **Financial Advice**: AI-driven recommendations
- **Anomaly Detection**: Unusual spending pattern alerts

## 📈 Development Phases

### Phase 1: Foundation (Current)
- ✅ Basic application setup
- ✅ Database schema
- ✅ Authentication
- 🔄 Core CRUD operations

### Phase 2: Features
- 📋 Budget management
- 📋 Analytics and reporting
- 📋 Receipt management

### Phase 3: Integration
- 📋 Bank integration
- 📋 UPI integration
- 📋 Account aggregator

### Phase 4: AI
- 📋 Machine learning models
- 📋 Intelligent categorization
- 📋 Financial insights

## 🛠️ Development Environment

- **IDE**: Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)
- **Database**: H2 (development), PostgreSQL (production)
- **Build Tool**: Maven
- **Java Version**: 17
- **Spring Boot**: 3.5.3

## 📝 Notes for Future Sessions

When resuming development:
1. The application is currently running successfully
2. All health endpoints are working
3. Database schema is created automatically
4. Sample data is loaded
5. Authentication is configured (admin/admin)
6. Next step is implementing repository interfaces and service layer

The foundation is solid and ready for feature development!

## 🔄 Configurable LLM Provider (OpenAI & Anthropic)

This project supports both **OpenAI** and **Anthropic (Claude)** as LLM providers for AI-powered analysis.

### Switching Providers

Edit `src/main/resources/application.yml`:

```yaml
ai:
  llm:
    provider: openai    # or 'anthropic'
    openai:
      api-key: ${OPENAI_API_KEY:}
      model: gpt-3.5-turbo
    anthropic:
      api-key: ${ANTHROPIC_API_KEY:}
      model: claude-3-haiku-20240307
```

### Setting API Keys

- **OpenAI:**  
  `export OPENAI_API_KEY="your-openai-key"`
- **Anthropic:**  
  `export ANTHROPIC_API_KEY="your-anthropic-key"`

Add these to your `~/.zshrc` or `~/.bashrc` for persistence.

### Getting API Keys
- [OpenAI API Keys](https://platform.openai.com/api-keys)
- [Anthropic API Keys](https://console.anthropic.com/)

### Testing the Integration
Use the `/api/ai/test` endpoint to verify your setup. Example:

```bash
curl -X POST http://localhost:8080/api/ai/test \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -d '{"userId": 1, "transactions": [{"description": "Coffee", "amount": 100, "category": "Food"}]}'
```

--- 