# Application Configuration Documentation

## Overview

The Bug Tracker application uses Spring Boot's configuration system with YAML files for different environments. The configuration is split into multiple files to support different deployment scenarios and development stages.

## Configuration Files Structure

### 1. Base Configuration (`application.yml`)
Contains common settings shared across all environments:
- Profile activation
- JPA common settings
- File upload configurations
- Thymeleaf template settings
- Server common settings
- Security defaults
- Async processing configuration

Key features:
```yaml
spring:
  profiles:
    active: dev  # Default profile
  jpa:
    open-in-view: false
  servlet:
    multipart:
      max-file-size: 10MB
```

### 2. Development Environment (`application-dev.yml`)
Configuration optimized for development work:
- Local database connection
- Debug logging enabled
- Development tools active
- SQL query logging
- Swagger documentation enabled

Key features:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bug_tracker_db
  jpa:
    show-sql: true
  devtools:
    restart:
      enabled: true
```

### 3. Production Environment (`application-prod.yml`)
Secure and optimized settings for production deployment:
- Environment variable based configuration
- SSL/TLS enabled
- Enhanced security settings
- Log rotation
- Performance optimizations
- Swagger disabled

Key features:
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    show-sql: false
```

### 4. Test Environment (`application-test.yml`)
Configuration for automated testing:
- H2 in-memory database
- Test-specific credentials
- Random server port
- Detailed SQL logging
- Flyway migrations disabled

Key features:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    hibernate:
      ddl-auto: create-drop
```

## Environment Variables

### Production Required Variables
- `DB_HOST`: Database host
- `DB_PORT`: Database port
- `DB_NAME`: Database name
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: JWT signing key
- `SSL_KEYSTORE_PATH`: SSL certificate path
- `SSL_KEYSTORE_PASSWORD`: SSL certificate password

### Optional Variables
- `SERVER_PORT`: Custom server port (default: 8080)
- `LOG_PATH`: Custom log directory (default: /var/log/bugtracker)

## Security Considerations

### Production Security Features
- SSL/TLS encryption enabled
- Secure headers configured
- CSRF protection active
- Session security settings
- Error message masking
- SQL query hiding
- Swagger UI disabled

### Development Security Features
- CSRF protection active
- Basic authentication enabled
- Full error messages
- H2 Console available
- Swagger UI enabled

## Running in Different Environments

### Development Mode
```bash
./mvnw spring-boot:run -Dspring.profiles.active=dev
```

### Production Mode
```bash
export DB_USERNAME=prod_user
export DB_PASSWORD=prod_password
# Set other required environment variables
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### Running Tests
```bash
./mvnw test
```

## Logging Configuration

### Development Logging
- Root level: INFO
- Application level: DEBUG
- SQL queries: DEBUG
- Security events: DEBUG

### Production Logging
- Root level: WARN
- Application level: INFO
- File-based logging
- Log rotation enabled
- Maximum file size: 10MB
- History: 30 days

## Database Configuration

### Development Database
- Auto-update schema
- Show SQL queries
- Format SQL output
- Development credentials

### Production Database
- Validate schema only
- Hide SQL queries
- Connection pool optimization
- Secure credentials from environment

### Test Database
- H2 in-memory database
- Create-drop schema
- Show SQL queries
- Test credentials

## Performance Tuning

### Production Optimizations
- Connection pool sizing
- Query logging disabled
- Template caching enabled
- Static resource caching
- Session timeout configuration

### Development Features
- Live reload enabled
- Template caching disabled
- Extended error messages
- Development tools active

## Maintenance Notes

1. **Environment Variables**
   - Use .env file for development
   - Use system environment for production
   - Never commit sensitive data

2. **SSL Certificates**
   - Required for production
   - Optional for development
   - Test certificates for testing

3. **Database Migrations**
   - Managed by Flyway
   - Enabled in production
   - Optional in development
   - Disabled in tests 
