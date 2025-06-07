# Troubleshooting Guide

## Common Issues and Solutions

### 1. Database Connection Issues

#### Symptoms
- Application fails to start with database connection errors
- "Connection refused" errors
- "Access denied" database messages

#### Solutions
1. **Check Database Credentials**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bug_tracker_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
   - Verify credentials in application-dev.yml
   - Ensure MySQL service is running
   - Check database exists: `CREATE DATABASE IF NOT EXISTS bug_tracker_db;`

2. **Database User Permissions**
   ```sql
   GRANT ALL PRIVILEGES ON bug_tracker_db.* TO 'your_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Connection Pool Issues**
   - Check HikariCP settings in application.yml
   - Verify maximum connections not exceeded
   - Monitor connection leaks

### 2. Authentication Problems

#### Symptoms
- Unable to log in
- Infinite redirect loops
- Access denied messages

#### Solutions
1. **Check Security Configuration**
   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {
       // Verify configuration matches documentation
   }
   ```

2. **Reset Admin Password**
   ```sql
   -- Password: administrator123456
   UPDATE users SET password='$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewqhrPtR6Z9dGKOq' 
   WHERE email='admin@bugtracker.com';
   ```

3. **Clear Browser Cache/Cookies**
   - Delete browser cookies
   - Clear local storage
   - Try incognito mode

### 3. Flyway Migration Failures

#### Symptoms
- "Migration checksum mismatch" errors
- "Validate failed" messages
- Database schema inconsistencies

#### Solutions
1. **Reset Flyway History**
   ```sql
   DELETE FROM flyway_schema_history;
   ```

2. **Clean and Migrate**
   ```bash
   ./mvnw flyway:clean
   ./mvnw flyway:migrate
   ```

3. **Verify Migration Files**
   - Check version numbers are sequential
   - Validate SQL syntax
   - Review migration history

### 4. Application Startup Issues

#### Symptoms
- Application fails to start
- Bean creation errors
- Component scan problems

#### Solutions
1. **Verify Dependencies**
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```
   - Run `./mvnw dependency:tree` to check conflicts
   - Update Maven dependencies
   - Clear Maven cache if needed

2. **Check Application Properties**
   - Validate YAML syntax
   - Verify property names
   - Check active profile

3. **Debug Mode**
   ```bash
   ./mvnw spring-boot:run -Ddebug=true
   ```

### 5. Performance Issues

#### Symptoms
- Slow response times
- High memory usage
- CPU spikes

#### Solutions
1. **Enable Debug Logging**
   ```yaml
   logging:
     level:
       org.hibernate.SQL: DEBUG
       org.hibernate.type.descriptor.sql.BasicBinder: TRACE
   ```

2. **Check Query Performance**
   - Review N+1 queries
   - Verify indexes exist
   - Monitor query execution plans

3. **Memory Settings**
   ```bash
   export JAVA_OPTS="-Xmx512m -Xms256m"
   ```

### 6. Testing Issues

#### Symptoms
- Failed unit tests
- Integration test errors
- Mock behavior problems

#### Solutions
1. **Test Database Configuration**
   ```yaml
   spring:
     datasource:
       url: jdbc:h2:mem:testdb
   ```

2. **Mock Configuration**
   ```java
   @MockBean
   private UserService userService;
   ```

3. **Test Environment**
   - Use correct application-test.yml
   - Clean test database
   - Reset mock states

### 7. Scheduler Issues

#### Symptoms
- Automated tasks not running
- Duplicate task execution
- Task timing problems

#### Solutions
1. **Verify Scheduler Configuration**
   ```java
   @EnableScheduling
   @Configuration
   public class SchedulerConfig {
       // Check configuration
   }
   ```

2. **Check Cron Expressions**
   ```java
   @Scheduled(cron = "0 0 0 * * ?") // Midnight
   ```

3. **Monitor Task Execution**
   - Enable scheduler logging
   - Check task completion
   - Verify timezone settings

## General Debugging Tips

### 1. Logging
```yaml
logging:
  level:
    root: INFO
    com.bugtracker: DEBUG
    org.springframework.security: DEBUG
```

### 2. Common Commands
```bash
# Clean and rebuild
./mvnw clean install

# Run with specific profile
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Run tests
./mvnw test
```

### 3. Health Checks
- `/actuator/health` endpoint
- Database connectivity
- External service status

### 4. Security Audit
- Check CSRF configuration
- Verify password encryption
- Review access controls

## Environment-Specific Issues

### Development
- IDE configuration
- Hot reload issues
- Local database setup

### Production
- SSL certificate configuration
- Environment variables
- Resource allocation

### Testing
- Test data preparation
- Mock service behavior
- Assertion failures

## Support Resources

### Documentation
- Spring Boot docs
- Project README
- API documentation

### Tools
- H2 Console: `/h2-console`
- Swagger UI: `/swagger-ui.html`
- Actuator endpoints

### Logs
- Application logs
- Server logs
- Database logs 
