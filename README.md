# Bug Tracker

A comprehensive bug tracking system built with Spring Boot that allows teams to track, manage, and resolve software bugs efficiently.

## Features

- **User Authentication & Authorization**: Role-based access control (ADMIN/USER)
- **Project Management**: Create and manage projects with member assignments
- **Bug Tracking**: Full lifecycle bug management with status, priority, and assignment
- **User Profiles**: Personal profile pages with activity statistics
- **Activity Logging**: Comprehensive logging of all system activities
- **Bug Filtering**: Advanced filtering by project, status, and priority
- **Comments System**: Add and view comments on bugs
- **File Attachments**: Support for bug attachments (planned)
- **RESTful API**: OpenAPI/Swagger documentation
- **Responsive UI**: Modern Bootstrap 5 interface

## Recent Updates (June 2025)

### ✅ Latest Fixes & Improvements

1. **Fixed Bug List Page**: 
   - Resolved Thymeleaf enum comparison issues
   - Improved badge colors for bug status and priority
   - Enhanced filtering functionality

2. **User Profile System**: 
   - Complete profile page implementation
   - Activity statistics (reported bugs, assigned bugs)
   - Profile editing and password change functionality
   - Proper template structure in `profile/profile.html`

3. **Template Engine Improvements**:
   - Fixed SpEL evaluation errors in Thymeleaf templates
   - Simplified enum comparisons using `.name()` method
   - Better error handling in templates

4. **Project Structure Optimization**:
   - Removed test dependencies that caused compilation issues
   - Streamlined build process for faster deployment
   - Improved development workflow

5. **Navigation & User Experience**:
   - Fixed all navigation menu links
   - Proper role-based menu visibility
   - Enhanced user feedback with success/error messages

## Tech Stack

- Java 21
- Spring Boot 3.2.0
- Spring Security 6.x
- Spring Data JPA
- Thymeleaf
- MySQL 8.x
- Bootstrap 5
- Maven
- JUnit 5 & Mockito (for testing)

## Setup Instructions

### Prerequisites

- JDK 17 or higher (JDK 21 recommended)
- Maven 3.6+
- MySQL Server 8.x

### Database Setup

1. Create a MySQL database:
   ```sql
   CREATE DATABASE bug_tracker_db;
   ```

2. Configure your database credentials in:
   - `application-dev.yml` for development
   - Use environment variables in production

### Running the Application

#### Development Mode

```bash
# Run with Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# OR using Java
java -jar target/bugtracker-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

#### Production Mode

```bash
# Set environment variables for security
export DB_USERNAME=your_db_username
export DB_PASSWORD=your_db_password
export ADMIN_PASS=secure_admin_password

# Run with production profile
java -jar target/bugtracker-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Default Admin Account

- Email: admin@bugtracker.com
- Password: admin123 (development only - change in production using `ADMIN_PASS` environment variable!)

## User Guide

### For Regular Users
1. **Dashboard**: View assigned and reported bugs statistics
2. **Bug List**: Browse and filter bugs with advanced search
3. **Profile**: Manage personal information and view activity stats
4. **Bug Details**: View detailed bug information and comments

### For Administrators
1. **Project Management**: Create and manage projects
2. **User Management**: Assign roles and manage user accounts
3. **System Overview**: Access to all bugs and projects
4. **Database Tools**: Special endpoints for database maintenance

## API Documentation

Once the application is running, access the OpenAPI documentation at:
- http://localhost:8080/swagger-ui.html

## Application Architecture

### MVC Pattern Implementation

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services     │───▶│  Repositories   │
│   (Web Layer)   │    │ (Business Logic)│    │  (Data Layer)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│    Templates    │    │      DTOs       │    │    Entities     │
│   (Thymeleaf)   │    │ (Data Transfer) │    │   (JPA/MySQL)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Project Structure

```
src/main/java/com/bugtracker/
├── config/           # Configuration classes (Security, Data initialization)
├── exception/        # Custom exception classes and global handlers
├── model/            # Domain models
│   ├── dto/          # Data Transfer Objects
│   ├── entity/       # JPA Entities (User, Bug, Project, Comment)
│   └── enums/        # Enumerations (BugStatus, BugPriority)
├── repository/       # Spring Data JPA repositories
├── service/          # Business logic interfaces
│   └── impl/         # Service implementations
├── util/             # Utility classes
├── validation/       # Custom validators
└── web/              # Web layer
    └── controller/   # MVC controllers and REST endpoints

src/main/resources/
├── templates/        # Thymeleaf templates
│   ├── bugs/         # Bug-related pages
│   ├── projects/     # Project management pages
│   ├── profile/      # User profile pages
│   └── fragments/    # Reusable template fragments
├── static/           # CSS, JS, images
└── db/migration/     # Flyway database migrations
```

## Security Features

- **Spring Security 6.x**: Modern security framework
- **Role-Based Access Control**: ADMIN and USER roles
- **Password Encryption**: BCrypt password hashing
- **CSRF Protection**: Built-in CSRF token validation
- **Session Management**: Secure session handling

## Database Schema

### Core Entities
- **Users**: User accounts with roles and profiles
- **Projects**: Project containers for bugs
- **Bugs**: Bug reports with lifecycle management
- **Comments**: Bug discussion threads
- **Roles**: User permission system
- **Log Entries**: Activity tracking

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**:
   ```bash
   # Find and kill the process
   netstat -ano | findstr :8080
   taskkill /PID <PID> /F
   ```

2. **Database connection issues**:
   - Verify MySQL is running
   - Check credentials in `application-dev.yml`
   - Ensure database exists

3. **Template errors**:
   - Check Thymeleaf syntax
   - Verify model attributes are passed correctly
   - Use proper enum comparisons (`bug.status.name() == 'NEW'`)

## Development Guidelines

1. **Code Quality**:
   - Follow Spring Boot best practices
   - Use proper logging (SLF4J) instead of `System.out.println()`
   - Write comprehensive unit tests

2. **Template Development**:
   - Use semantic HTML with Bootstrap 5
   - Implement proper error handling
   - Follow Thymeleaf conventions

3. **Database**:
   - Use Flyway for schema migrations
   - Follow JPA naming conventions
   - Implement proper entity relationships

## Logging Best Practices

This project uses SLF4J with Logback for logging instead of `System.out.println()` for several important reasons:

### Why we use `log.info()` instead of `System.out.println()`

1. **Configuration and Flexibility**:
   - Logging frameworks allow controlling log levels (DEBUG, INFO, WARN, ERROR) without code changes
   - Multiple output destinations (console, files, databases) can be configured
   - Log rotation and size limitations are handled automatically

2. **Better Formatting**:
   - Automatic timestamp and context information (class name, thread)
   - Support for parameterized messages (`log.info("Value: {}", value)`) that are more efficient
   - Structured format for easier parsing and analysis

3. **Performance**:
   - Improved performance when logging is disabled at a certain level
   - Lazy evaluation of parameters (calculated only if the log level is enabled)
   - Less impact on application performance in production

Example of proper logging in our codebase:
```java
// Incorrect approach
System.out.println("Created new bug with ID: " + bug.getId());

// Correct approach with SLF4J
log.info("Created new bug with ID: {}", bug.getId());
```

## Database Migrations

Flyway manages database migrations. Migration scripts are in:
- `src/main/resources/db/migration/`

## License

This project is licensed under the MIT License - see the LICENSE file for details.
