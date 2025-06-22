# System Design Document - AI-Powered Expense Tracker

## 🏗️ System Architecture Overview

### High-Level Architecture
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Backend       │    │   External      │
│   (Future)      │◄──►│   Spring Boot   │◄──►│   Services      │
│                 │    │   Application   │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Database      │
                       │   (H2/PostgreSQL)│
                       └─────────────────┘
```

### Technology Stack
- **Backend Framework**: Spring Boot 3.5.3
- **Language**: Java 17
- **Database**: H2 (Development), PostgreSQL (Production)
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security (Basic Auth → JWT → OAuth2)
- **Build Tool**: Maven
- **API Documentation**: Swagger/OpenAPI (Future)

## 📊 Database Schema Design

### Core Tables (Implemented)

#### 1. Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    phone VARCHAR(15),
    date_of_birth DATE,
    monthly_income DECIMAL(12,2),
    currency VARCHAR(3) DEFAULT 'INR',
    timezone VARCHAR(50) DEFAULT 'Asia/Kolkata',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Purpose**: User management with financial preferences
**Key Features**: 
- Email-based authentication
- Financial profile (income, currency, timezone)
- Audit trail (created_at, updated_at)

#### 2. Categories Table
```sql
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    icon VARCHAR(50),
    color VARCHAR(7),
    parent_id BIGINT,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);
```

**Purpose**: Hierarchical expense categorization
**Key Features**:
- Hierarchical structure (parent-child relationships)
- Visual customization (icon, color)
- Default categories for new users

#### 3. Transactions Table
```sql
CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    transaction_type ENUM('EXPENSE', 'INCOME', 'TRANSFER') NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_time TIME,
    description TEXT,
    notes TEXT,
    category_id BIGINT,
    user_category_id BIGINT,
    source_type ENUM('MANUAL', 'UPI', 'BANK', 'ACCOUNT_AGGREGATOR', 'CREDIT_CARD') DEFAULT 'MANUAL',
    source_id VARCHAR(255),
    external_reference VARCHAR(255),
    bank_name VARCHAR(100),
    account_number VARCHAR(50),
    is_immutable BOOLEAN DEFAULT FALSE,
    is_recurring BOOLEAN DEFAULT FALSE,
    recurring_pattern VARCHAR(50),
    last_synced_at TIMESTAMP NULL,
    sync_status ENUM('PENDING', 'SYNCED', 'FAILED') DEFAULT 'PENDING',
    tags JSON,
    location VARCHAR(255),
    receipt_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (user_category_id) REFERENCES user_categories(id) ON DELETE SET NULL
);
```

**Purpose**: Core transaction tracking with source integration
**Key Features**:
- Multiple transaction types (expense, income, transfer)
- Source tracking (manual, UPI, bank, etc.)
- Recurring transaction support
- Location and receipt tracking
- JSON tags for flexible metadata

#### 4. UserCategories Table
```sql
CREATE TABLE user_categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    custom_name VARCHAR(100),
    custom_icon VARCHAR(50),
    custom_color VARCHAR(7),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_category (user_id, category_id)
);
```

**Purpose**: User-specific category customization
**Key Features**:
- Override default category properties
- User-specific naming and styling
- Active/inactive status

### Future Tables (Planned)

#### 5. Budgets Table
```sql
CREATE TABLE budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    period_type ENUM('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE,
    category_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    alert_threshold DECIMAL(5,2) DEFAULT 80.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);
```

#### 6. FinancialGoals Table
```sql
CREATE TABLE financial_goals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    target_amount DECIMAL(12,2) NOT NULL,
    current_amount DECIMAL(12,2) DEFAULT 0.00,
    currency VARCHAR(3) DEFAULT 'INR',
    target_date DATE,
    goal_type ENUM('SAVINGS', 'DEBT_PAYOFF', 'INVESTMENT', 'PURCHASE') NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') DEFAULT 'MEDIUM',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### 7. BankAccounts Table
```sql
CREATE TABLE bank_accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    bank_name VARCHAR(100) NOT NULL,
    account_type ENUM('SAVINGS', 'CURRENT', 'CREDIT_CARD', 'LOAN') NOT NULL,
    account_number VARCHAR(50),
    masked_account_number VARCHAR(50),
    balance DECIMAL(12,2),
    currency VARCHAR(3) DEFAULT 'INR',
    is_active BOOLEAN DEFAULT TRUE,
    last_synced_at TIMESTAMP NULL,
    sync_status ENUM('PENDING', 'SYNCED', 'FAILED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### 8. AIAnalysis Table
```sql
CREATE TABLE ai_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    analysis_type ENUM('SPENDING_PATTERN', 'CATEGORIZATION', 'ANOMALY_DETECTION', 'RECOMMENDATION') NOT NULL,
    analysis_data JSON NOT NULL,
    confidence_score DECIMAL(3,2),
    is_applied BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

#### 9. SyncStatus Table
```sql
CREATE TABLE sync_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    source_type ENUM('UPI', 'BANK', 'ACCOUNT_AGGREGATOR', 'CREDIT_CARD') NOT NULL,
    source_id VARCHAR(255) NOT NULL,
    last_sync_at TIMESTAMP NULL,
    sync_status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_sync_status (user_id, source_type, source_id)
);
```

## 🏛️ Backend Architecture

### Layer Architecture
```
┌─────────────────────────────────────────────────────────────┐
│                    Controller Layer                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │ Transaction │ │   Category  │ │    User     │          │
│  │ Controller  │ │ Controller  │ │ Controller  │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Service Layer                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │ Transaction │ │   Category  │ │    User     │          │
│  │   Service   │ │   Service   │ │   Service   │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   Repository Layer                          │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │ Transaction │ │   Category  │ │    User     │          │
│  │ Repository  │ │ Repository  │ │ Repository  │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    Entity Layer                             │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐          │
│  │ Transaction │ │   Category  │ │    User     │          │
│  │   Entity    │ │   Entity    │ │   Entity    │          │
│  └─────────────┘ └─────────────┘ └─────────────┘          │
└─────────────────────────────────────────────────────────────┘
```

### Current Implementation Status

#### ✅ Implemented
- **Entity Layer**: All core entities (User, Category, Transaction, UserCategory)
- **Basic Security**: Spring Security with Basic Authentication
- **Health Controller**: Application and database health checks
- **Configuration**: YAML-based configuration with H2 database

#### 🔄 In Progress
- **Repository Layer**: Spring Data JPA repositories
- **Service Layer**: Business logic implementation
- **Controller Layer**: REST API endpoints

#### 📋 Planned
- **Enhanced Security**: JWT tokens, role-based access
- **Validation**: Input validation and error handling
- **Testing**: Unit and integration tests
- **Documentation**: Swagger/OpenAPI

## 🔐 Security Architecture

### Current Security (Basic Authentication)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/health/**", "/actuator/health").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
```

### Future Security Enhancements

#### Phase 1: JWT Authentication
- JWT token generation and validation
- Token refresh mechanism
- Stateless authentication

#### Phase 2: OAuth2 Integration
- Social login (Google, Facebook)
- Bank OAuth for account aggregation
- Third-party service integration

#### Phase 3: Advanced Security
- Role-based access control (RBAC)
- API rate limiting
- Data encryption at rest
- Audit logging

## 🔗 API Design

### REST API Endpoints

#### Current Endpoints
```
GET  /api/health          - Application health check
GET  /api/health/db       - Database health check
GET  /actuator/health     - Spring Boot actuator health
```

#### Planned Endpoints

##### User Management
```
GET    /api/users                    - Get all users (admin)
GET    /api/users/{id}               - Get user by ID
POST   /api/users                    - Create new user
PUT    /api/users/{id}               - Update user
DELETE /api/users/{id}               - Delete user
GET    /api/users/profile            - Get current user profile
PUT    /api/users/profile            - Update current user profile
```

##### Transaction Management
```
GET    /api/transactions             - Get user transactions
GET    /api/transactions/{id}        - Get transaction by ID
POST   /api/transactions             - Create new transaction
PUT    /api/transactions/{id}        - Update transaction
DELETE /api/transactions/{id}        - Delete transaction
GET    /api/transactions/analytics   - Get transaction analytics
```

##### Category Management
```
GET    /api/categories               - Get all categories
GET    /api/categories/{id}          - Get category by ID
POST   /api/categories               - Create new category
PUT    /api/categories/{id}          - Update category
DELETE /api/categories/{id}          - Delete category
GET    /api/categories/user          - Get user-specific categories
```

##### Budget Management
```
GET    /api/budgets                  - Get user budgets
GET    /api/budgets/{id}             - Get budget by ID
POST   /api/budgets                  - Create new budget
PUT    /api/budgets/{id}             - Update budget
DELETE /api/budgets/{id}             - Delete budget
GET    /api/budgets/analytics        - Get budget analytics
```

### API Response Format
```json
{
  "success": true,
  "data": {
    // Response data
  },
  "message": "Operation successful",
  "timestamp": "2025-06-22T17:40:30.123Z"
}
```

### Error Response Format
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      "Amount must be greater than 0",
      "Category is required"
    ]
  },
  "timestamp": "2025-06-22T17:40:30.123Z"
}
```

## 🤖 AI Integration Architecture

### AI Services Design

#### 1. Transaction Categorization
```java
@Service
public class AICategorizationService {
    public Category categorizeTransaction(Transaction transaction) {
        // Use ML model to predict category
        // Return predicted category with confidence score
    }
}
```

#### 2. Spending Pattern Analysis
```java
@Service
public class SpendingAnalysisService {
    public SpendingInsights analyzeSpendingPatterns(Long userId) {
        // Analyze historical data
        // Identify patterns and trends
        // Generate insights and recommendations
    }
}
```

#### 3. Anomaly Detection
```java
@Service
public class AnomalyDetectionService {
    public List<Anomaly> detectAnomalies(Long userId) {
        // Detect unusual spending patterns
        // Flag potential fraud or errors
        // Generate alerts
    }
}
```

### AI Model Integration

#### Phase 1: Rule-Based Categorization
- Keyword matching
- Amount-based rules
- Merchant name analysis

#### Phase 2: Machine Learning
- Supervised learning for categorization
- Unsupervised learning for pattern detection
- Natural language processing for description analysis

#### Phase 3: Advanced AI
- Deep learning models
- Real-time learning from user feedback
- Predictive analytics

## 🏦 Bank Integration Architecture

### UPI Integration
```java
@Service
public class UPIIntegrationService {
    public List<Transaction> fetchUPITransactions(String upiId) {
        // Integrate with UPI APIs
        // Fetch transaction history
        // Map to internal transaction format
    }
}
```

### Account Aggregator Framework
```java
@Service
public class AccountAggregatorService {
    public List<Transaction> fetchBankTransactions(String consentId) {
        // Use Account Aggregator APIs
        // Fetch data from multiple banks
        // Handle consent management
    }
}
```

### Credit Card Integration
```java
@Service
public class CreditCardService {
    public List<Transaction> parseCreditCardStatement(String statementFile) {
        // Parse PDF/CSV statements
        // Extract transaction data
        // Map to internal format
    }
}
```

## 📊 Analytics and Reporting

### Analytics Services
```java
@Service
public class AnalyticsService {
    public MonthlyReport generateMonthlyReport(Long userId, int year, int month) {
        // Aggregate transaction data
        // Calculate spending by category
        // Compare with budgets
        // Generate insights
    }
}
```

### Reporting Features
- Monthly/Yearly spending summaries
- Category-wise analysis
- Budget vs actual comparison
- Trend analysis
- Export functionality (PDF, Excel)

## 🚀 Deployment Architecture

### Development Environment
- **Database**: H2 in-memory
- **Application**: Local Spring Boot
- **Build**: Maven
- **IDE**: Any Java IDE

### Production Environment
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Load Balancer │    │   Application   │    │   PostgreSQL    │
│   (Nginx)       │◄──►│   Instances     │◄──►│   Database      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   Redis Cache   │
                       └─────────────────┘
```

### Containerization
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/expense-tracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### CI/CD Pipeline
1. **Build**: Maven build and test
2. **Test**: Unit and integration tests
3. **Security**: Vulnerability scanning
4. **Deploy**: Container deployment
5. **Monitor**: Health checks and monitoring

## 📈 Performance Considerations

### Database Optimization
- Proper indexing on frequently queried columns
- Query optimization and caching
- Connection pooling (HikariCP)
- Read replicas for analytics

### Application Optimization
- Response caching with Redis
- Async processing for heavy operations
- Pagination for large datasets
- Compression for API responses

### Monitoring and Observability
- Application metrics (Micrometer)
- Database performance monitoring
- API response time tracking
- Error rate monitoring

## 🔄 Feature Rollout Strategy

### Phase 1: Foundation (Current)
- ✅ Basic application setup
- ✅ Database schema
- ✅ Authentication
- 🔄 Core CRUD operations

### Phase 2: Enhanced Features
- 📋 Budget management
- 📋 Analytics and reporting
- 📋 Receipt management
- 📋 User preferences

### Phase 3: Bank Integration
- 📋 UPI integration
- 📋 Account aggregator
- 📋 Credit card parsing
- 📋 Automated categorization

### Phase 4: AI Integration
- 📋 ML-based categorization
- 📋 Spending pattern analysis
- 📋 Anomaly detection
- 📋 Financial recommendations

## 🛡️ Security Considerations

### Data Protection
- Encryption at rest and in transit
- PII data masking
- Secure password storage (BCrypt)
- Regular security audits

### Access Control
- Role-based permissions
- API rate limiting
- Session management
- Audit logging

### Compliance
- GDPR compliance for EU users
- RBI guidelines for financial data
- Data retention policies
- Privacy by design

---

This system design provides a comprehensive foundation for building a scalable, secure, and feature-rich expense tracking application with AI capabilities and bank integration. 