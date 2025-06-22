# Security Documentation

## 🔒 Security Overview

This document outlines the security measures implemented in the Expense Tracker application and provides guidance for secure deployment.

## 🛡️ Security Measures Implemented

### 1. Authentication & Authorization
- ✅ **Spring Security** with BCrypt password hashing
- ✅ **Method-level security** with `@PreAuthorize` annotations
- ✅ **Role-based access control** (ADMIN role)
- ✅ **User data isolation** - users can only access their own data

### 2. Input Validation
- ✅ **Bean Validation** with `@Valid` annotations
- ✅ **Input sanitization** for transaction descriptions
- ✅ **Size constraints** on request payloads
- ✅ **Type validation** for numeric parameters

### 3. API Security
- ✅ **CSRF protection** enabled (except for H2 console)
- ✅ **CORS configuration** with restricted origins
- ✅ **Security headers** (HSTS, Content-Type-Options)
- ✅ **HTTP-only cookies** for session management

### 4. Data Protection
- ✅ **Environment variables** for sensitive configuration
- ✅ **API key masking** in logs
- ✅ **Reduced logging levels** in production
- ✅ **Secure session configuration**

## 🚨 Critical Security Issues Fixed

### 1. Hardcoded Credentials ❌ → ✅
**Before:**
```yaml
security:
  user:
    name: admin
    password: admin
```

**After:**
```yaml
security:
  user:
    name: ${ADMIN_USERNAME:admin}
    password: ${ADMIN_PASSWORD:admin}
```

### 2. CSRF Protection ❌ → ✅
**Before:**
```java
.csrf(csrf -> csrf.disable());
```

**After:**
```java
.csrf(csrf -> csrf
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
    .ignoringRequestMatchers("/h2-console/**")
);
```

### 3. H2 Console Exposure ❌ → ✅
**Before:**
```yaml
h2:
  console:
    enabled: true
```

**After:**
```yaml
h2:
  console:
    enabled: false  # Disabled in production
```

### 4. Excessive Logging ❌ → ✅
**Before:**
```yaml
logging:
  level:
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
```

**After:**
```yaml
logging:
  level:
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
```

## 🔧 Production Security Checklist

### Environment Variables
Set these environment variables in production:

```bash
# Admin credentials
export ADMIN_USERNAME=your_admin_username
export ADMIN_PASSWORD=your_secure_password

# API keys
export OPENAI_API_KEY=your_openai_key
export ANTHROPIC_API_KEY=your_anthropic_key

# Database (if using external database)
export DB_URL=your_database_url
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
```

### HTTPS Configuration
Enable HTTPS in production:

```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
  servlet:
    session:
      cookie:
        secure: true  # Enable secure cookies
```

### Database Security
- Use external database (PostgreSQL, MySQL) instead of H2
- Enable database encryption
- Use connection pooling with proper timeouts
- Implement database user with minimal privileges

### API Security
- Implement rate limiting (consider using Spring Cloud Gateway)
- Add API versioning
- Implement request/response logging (without sensitive data)
- Add request ID tracking for audit trails

## 🚨 Remaining Security Considerations

### 1. Rate Limiting
**Status:** Not implemented
**Recommendation:** Implement rate limiting using:
- Spring Cloud Gateway
- Bucket4j library
- Redis-based rate limiting

### 2. Audit Logging
**Status:** Basic logging only
**Recommendation:** Implement comprehensive audit logging:
- User actions
- Data access patterns
- Security events
- API usage metrics

### 3. Data Encryption
**Status:** Not implemented
**Recommendation:** 
- Encrypt sensitive data at rest
- Implement field-level encryption for PII
- Use database encryption features

### 4. API Documentation Security
**Status:** Not implemented
**Recommendation:**
- Secure API documentation endpoints
- Implement API key authentication for docs
- Add security examples in documentation

## 🔍 Security Testing

### Automated Security Tests
```bash
# Run security tests
./mvnw test -Dtest=SecurityTest

# Run OWASP dependency check
./mvnw org.owasp:dependency-check-maven:check
```

### Manual Security Testing
1. **Authentication Testing**
   - Test invalid credentials
   - Test session timeout
   - Test concurrent sessions

2. **Authorization Testing**
   - Test access to other users' data
   - Test role-based access
   - Test API endpoint permissions

3. **Input Validation Testing**
   - Test SQL injection attempts
   - Test XSS payloads
   - Test oversized payloads

4. **API Security Testing**
   - Test CSRF protection
   - Test CORS configuration
   - Test rate limiting (when implemented)

## 📋 Security Headers

The application implements the following security headers:

```java
.headers(headers -> headers
    .frameOptions().sameOrigin()
    .contentTypeOptions().and()
    .httpStrictTransportSecurity(hsts -> hsts
        .maxAgeInSeconds(31536000)
    )
);
```

## 🔐 API Key Management

### Best Practices
1. **Never commit API keys** to version control
2. **Use environment variables** for all secrets
3. **Rotate API keys** regularly
4. **Monitor API usage** for anomalies
5. **Use least privilege** principle for API permissions

### Key Rotation Process
1. Generate new API key
2. Update environment variable
3. Restart application
4. Verify functionality
5. Revoke old key after verification

## 📞 Security Contact

For security issues or questions:
- Create a GitHub issue with `[SECURITY]` prefix
- Contact: [Your Security Contact]
- PGP Key: [Your PGP Key]

## 📚 Additional Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [OWASP Cheat Sheet Series](https://cheatsheetseries.owasp.org/)
- [Security Headers](https://securityheaders.com/)

---

**Last Updated:** December 2024
**Version:** 1.0.0 