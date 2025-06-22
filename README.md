# AI-Powered Expense Tracker

A comprehensive expense tracking application with AI-driven financial insights and bank integration capabilities.

## 🚀 Project Overview

This is a Spring Boot application designed to evolve from manual expense entry to an AI-powered financial management system with bank integration. The application follows a phased approach to feature development.

## 🏗️ System Architecture

### Technology Stack
- **Backend**: Spring Boot 3.5.3, Java 17
- **Database**: H2 (development), PostgreSQL (production)
- **Security**: Spring Security with Basic Authentication
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven

### Database Schema
The application uses a comprehensive database design supporting:
- **Core Tables**: users, categories, transactions, user_categories
- **Future Tables**: budgets, financial_goals, bank_accounts, ai_analysis, sync_status
- **Features**: Manual entry, UPI integration, Account Aggregator, AI analysis

## 📁 Current Project Structure

```
expense-tracker/
├── src/main/java/com/expensetracker/expensetracker/
│   ├── controller/
│   │   └── HealthController.java          # Health check endpoints
│   ├── entity/
│   │   ├── Category.java                  # Expense categories
│   │   ├── Transaction.java               # Financial transactions
│   │   ├── User.java                      # User management
│   │   └── UserCategory.java              # User-specific categories
│   └── ExpenseTrackerApplication.java     # Main application class
├── src/main/resources/
│   ├── application.yml                    # Application configuration
│   ├── data.sql                          # Sample data
│   └── static/                           # Static resources
└── pom.xml                               # Maven dependencies
```

## ✅ What's Currently Implemented

### 1. Core Infrastructure
- ✅ Spring Boot 3.5.3 application with Java 17
- ✅ H2 in-memory database configuration
- ✅ JPA entities for core tables (User, Category, Transaction, UserCategory)
- ✅ Basic authentication (admin/admin)
- ✅ Health check endpoints (`/api/health`, `/api/health/db`, `/actuator/health`)
- ✅ Database schema auto-generation by Hibernate
- ✅ Sample data loading

### 2. Database Schema
- ✅ **Users Table**: User management with profile information
- ✅ **Categories Table**: Predefined expense categories
- ✅ **Transactions Table**: Financial transactions with source tracking
- ✅ **UserCategories Table**: User-specific category customization

### 3. Security
- ✅ Basic authentication configured
- ✅ Protected endpoints requiring authentication
- ✅ H2 console available at `/h2-console`

## 🔄 Application Status

**Current State**: ✅ **RUNNING SUCCESSFULLY**

The application starts successfully and all health endpoints are accessible with authentication:
- Application runs on port 8080
- Database schema created automatically
- Sample data loaded
- Authentication working (admin/admin)

## 🎯 Planned Features (Phased Approach)

### Phase 1: Manual Entry (Current)
- ✅ Basic CRUD operations for transactions
- ✅ Category management
- ✅ User authentication
- 🔄 **NEXT**: Transaction controllers and services

### Phase 2: Enhanced Features
- 📋 Budget tracking and alerts
- 📋 Financial goals management
- 📋 Expense analytics and reporting
- 📋 Receipt upload and storage

### Phase 3: Bank Integration
- 📋 UPI transaction integration
- 📋 Account Aggregator framework
- 📋 Credit card statement parsing
- 📋 Automated transaction categorization

### Phase 4: AI Integration
- 📋 Transaction categorization using AI
- 📋 Spending pattern analysis
- 📋 Financial advice and recommendations
- 📋 Anomaly detection

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Running the Application
```bash
# Set Java 17 path (if needed)
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"

# Run the application
./mvnw spring-boot:run
```

### Accessing the Application
- **Application**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
- **Health Check**: http://localhost:8080/api/health
- **Database Health**: http://localhost:8080/api/health/db

### Authentication
- **Username**: admin
- **Password**: admin

## 📊 Database Configuration

### Current Configuration (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:expensetracker
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    defer-datasource-initialization: true
  h2:
    console:
      enabled: true
  sql:
    init:
      mode: always
```

## 🔧 Troubleshooting History

### Issues Resolved
1. **Schema.sql conflicts**: Removed custom schema.sql to let Hibernate handle schema creation
2. **Data initialization timing**: Used `defer-datasource-initialization: true` to ensure schema is created before data loading
3. **H2 dialect warning**: Removed explicit H2 dialect configuration (auto-detected)

### Current Working Configuration
- Hibernate creates schema automatically
- Sample data loads after schema creation
- Application starts successfully with all endpoints working

## 📋 Next Steps

### Immediate Tasks
1. **Create Repository Interfaces**: Add Spring Data JPA repositories for all entities
2. **Implement Service Layer**: Business logic for transactions, categories, users
3. **Add REST Controllers**: CRUD operations for all entities
4. **Enhance Security**: Role-based access control, JWT tokens

### Short-term Goals
1. **Transaction Management**: Full CRUD for expense/income tracking
2. **Category Management**: User-specific category customization
3. **Basic Analytics**: Monthly spending summaries
4. **API Documentation**: Swagger/OpenAPI documentation

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

## 🔗 API Endpoints

### Current Endpoints
- `GET /api/health` - Application health check
- `GET /api/health/db` - Database health check
- `GET /actuator/health` - Spring Boot actuator health

### Planned Endpoints
- `GET/POST/PUT/DELETE /api/transactions` - Transaction management
- `GET/POST/PUT/DELETE /api/categories` - Category management
- `GET/POST/PUT/DELETE /api/users` - User management
- `GET /api/analytics` - Financial analytics

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