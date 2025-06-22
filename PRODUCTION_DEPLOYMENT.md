# Production Deployment Guide

## 🚀 Overview

This guide provides step-by-step instructions for securely deploying the Expense Tracker application to production.

## 📋 Prerequisites

- Java 17 or higher
- PostgreSQL 12+ or MySQL 8+
- Redis (for session management and caching)
- SSL certificate
- Domain name
- CI/CD pipeline (optional but recommended)

## 🔧 Environment Setup

### 1. Database Configuration

Replace H2 with a production database:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/expense_tracker
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate  # Don't auto-create tables in production
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
```

### 2. Security Configuration

Create `application-prod.yml`:

```yaml
spring:
  profiles: prod
  
  # Disable H2 console completely
  h2:
    console:
      enabled: false
  
  # Production security settings
  security:
    user:
      name: ${ADMIN_USERNAME}
      password: ${ADMIN_PASSWORD}
  
  # Session management
  session:
    store-type: redis
    redis:
      namespace: expense-tracker:sessions
      flush-mode: on_save

# Server configuration
server:
  port: 8443
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: expense-tracker
  servlet:
    session:
      cookie:
        http-only: true
        secure: true
        same-site: strict
        max-age: 3600

# Logging configuration
logging:
  level:
    root: WARN
    com.expensetracker: INFO
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %-5level - %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} - %-5level - %logger{36} - %msg%n"
  file:
    name: logs/expense-tracker.log
    max-size: 100MB
    max-history: 30

# Actuator configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  security:
    enabled: true

# Redis configuration
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
```

### 3. Environment Variables

Create `.env` file (never commit this):

```bash
# Database
DB_URL=jdbc:postgresql://localhost:5432/expense_tracker
DB_USERNAME=expense_tracker_user
DB_PASSWORD=your_secure_db_password

# Admin credentials
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_very_secure_admin_password

# API Keys
OPENAI_API_KEY=your_openai_api_key
ANTHROPIC_API_KEY=your_anthropic_api_key

# SSL
SSL_KEYSTORE_PASSWORD=your_keystore_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password

# Application
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8443
```

## 🐳 Docker Deployment

### Dockerfile

```dockerfile
FROM openjdk:17-jre-slim

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Create app directory
WORKDIR /app

# Copy application jar
COPY target/expense-tracker-*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

# Switch to app user
USER appuser

# Expose port
EXPOSE 8443

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8443/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8443:8443"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_URL=jdbc:postgresql://db:5432/expense_tracker
      - DB_USERNAME=${DB_USERNAME}
      - DB_PASSWORD=${DB_PASSWORD}
      - ADMIN_USERNAME=${ADMIN_USERNAME}
      - ADMIN_PASSWORD=${ADMIN_PASSWORD}
      - OPENAI_API_KEY=${OPENAI_API_KEY}
      - ANTHROPIC_API_KEY=${ANTHROPIC_API_KEY}
      - REDIS_HOST=redis
    depends_on:
      - db
      - redis
    volumes:
      - ./logs:/app/logs
      - ./ssl:/app/ssl
    restart: unless-stopped

  db:
    image: postgres:15
    environment:
      - POSTGRES_DB=expense_tracker
      - POSTGRES_USER=${DB_USERNAME}
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    ports:
      - "5432:5432"
    restart: unless-stopped

  redis:
    image: redis:7-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    restart: unless-stopped

volumes:
  postgres_data:
  redis_data:
```

## 🔒 Security Hardening

### 1. SSL/TLS Configuration

Generate SSL certificate:

```bash
# Generate keystore
keytool -genkeypair -alias expense-tracker \
  -keyalg RSA -keysize 2048 \
  -storetype PKCS12 \
  -keystore keystore.p12 \
  -validity 365 \
  -storepass your_keystore_password
```

### 2. Database Security

```sql
-- Create database user with minimal privileges
CREATE USER expense_tracker_user WITH PASSWORD 'your_secure_password';
GRANT CONNECT ON DATABASE expense_tracker TO expense_tracker_user;
GRANT USAGE ON SCHEMA public TO expense_tracker_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO expense_tracker_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO expense_tracker_user;

-- Enable SSL for database connections
ALTER SYSTEM SET ssl = on;
ALTER SYSTEM SET ssl_ciphers = 'HIGH:MEDIUM:+3DES:!aNULL';
```

### 3. Firewall Configuration

```bash
# Allow only necessary ports
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 8443/tcp  # Application HTTPS
sudo ufw allow 5432/tcp  # PostgreSQL (if external)
sudo ufw allow 6379/tcp  # Redis (if external)
sudo ufw enable
```

### 4. System Security

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install security tools
sudo apt install -y fail2ban ufw

# Configure fail2ban
sudo cp /etc/fail2ban/jail.conf /etc/fail2ban/jail.local
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

## 📊 Monitoring & Logging

### 1. Application Monitoring

Add monitoring dependencies to `pom.xml`:

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

### 2. Log Aggregation

Configure log forwarding to ELK stack or similar:

```yaml
logging:
  pattern:
    json: '{"timestamp":"%d{yyyy-MM-dd HH:mm:ss.SSS}","level":"%-5level","logger":"%logger{36}","message":"%msg","trace_id":"%X{traceId:-}","span_id":"%X{spanId:-}"}'
  file:
    name: logs/expense-tracker.json
```

### 3. Health Checks

```bash
# Application health
curl -k https://localhost:8443/actuator/health

# Database health
curl -k https://localhost:8443/actuator/health/db

# Custom health check
curl -k https://localhost:8443/actuator/health/ai
```

## 🔄 CI/CD Pipeline

### GitHub Actions Example

```yaml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Build with Maven
        run: ./mvnw clean package -DskipTests
      
      - name: Run security scan
        run: ./mvnw org.owasp:dependency-check-maven:check
      
      - name: Deploy to server
        run: |
          # Deploy script
          scp target/expense-tracker-*.jar user@server:/app/
          ssh user@server 'sudo systemctl restart expense-tracker'
```

## 🚨 Incident Response

### 1. Security Incident Checklist

- [ ] Isolate affected systems
- [ ] Preserve evidence
- [ ] Assess impact
- [ ] Notify stakeholders
- [ ] Implement temporary fixes
- [ ] Investigate root cause
- [ ] Implement permanent fixes
- [ ] Update security measures
- [ ] Document lessons learned

### 2. Backup Strategy

```bash
# Database backup
pg_dump -h localhost -U expense_tracker_user expense_tracker > backup_$(date +%Y%m%d_%H%M%S).sql

# Application backup
tar -czf app_backup_$(date +%Y%m%d_%H%M%S).tar.gz /app/

# Log backup
tar -czf logs_backup_$(date +%Y%m%d_%H%M%S).tar.gz /app/logs/
```

## 📈 Performance Optimization

### 1. JVM Tuning

```bash
java -Xms2g -Xmx4g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+UseStringDeduplication \
  -jar app.jar
```

### 2. Database Optimization

```sql
-- Create indexes for performance
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_ai_analysis_user ON ai_analysis(user_id);

-- Analyze tables
ANALYZE transactions;
ANALYZE ai_analysis;
```

## ✅ Deployment Checklist

- [ ] Environment variables configured
- [ ] Database migrated and tested
- [ ] SSL certificate installed
- [ ] Firewall configured
- [ ] Monitoring setup
- [ ] Backup strategy implemented
- [ ] Security scan completed
- [ ] Load testing performed
- [ ] Documentation updated
- [ ] Team trained on deployment

---

**Last Updated:** December 2024
**Version:** 1.0.0 