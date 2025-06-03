# Bug Tracker

A comprehensive bug tracking system built with Spring Boot that allows teams to track, manage, and resolve software bugs efficiently.

## Features

- **User Authentication & Authorization**: Role-based access control (ADMIN/USER)
- **Project Management**: Create and manage projects with member assignments
- **Bug Tracking**: Full lifecycle bug management with status, priority, and assignment
- **User Profiles**: Personal profile pages with activity statistics
- **Activity Logging**: Comprehensive logging of all system activities
- **Bug Filtering**: Advanced filtering by project, status, and priority
- **Comments System**: Add and view comments on bugs with full CRUD operations
- **File Attachments**: Support for bug attachments (planned)
- **RESTful API**: OpenAPI/Swagger documentation
- **Responsive UI**: Modern Bootstrap 5 interface

## Current Status (December 2024)

### ✅ Fully Working Features

**Core Functionality:**
- ✅ User registration and authentication system
- ✅ Role-based access control (ADMIN/USER permissions)
- ✅ Project management (admin-only creation and management)
- ✅ Complete bug lifecycle management (create, update, assign, status tracking)
- ✅ User profile system with activity statistics
- ✅ Comment system with create, edit, and delete functionality


**Application Architecture:**
- ✅ Clean MVC pattern implementation
- ✅ Proper service layer separation
- ✅ JPA entity relationships working correctly
- ✅ Spring Security 6.x integration complete
- ✅ Bootstrap 5 responsive UI implementation

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.2.0, Spring Security 6.x, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, HTML5, CSS3
- **Database**: MySQL 8.x with Hibernate ORM
- **Build Tool**: Maven
- **Server**: Embedded Tomcat

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

### Access the Application

Once running, the application is available at:
- **URL**: http://localhost:8080
- **Admin Account**: admin@bugtracker.com / admin123 (change password in production!)

## User Guide

### For Regular Users
1. **Dashboard**: View assigned and reported bugs statistics
2. **Bug List**: Browse and filter bugs with advanced search
3. **Bug Management**: Create, edit, and comment on bugs
4. **Profile**: Manage personal information and view activity statistics

### For Administrators
1. **Project Management**: Create and manage projects with member assignments
2. **User Management**: Full access to all users and projects
3. **System Overview**: Access to all bugs and projects across the system
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
└── application*.yml  # Configuration files
```

## Security Features

- **Spring Security 6.x**: Modern security framework
- **Role-Based Access Control**: ADMIN and USER roles with proper authorization
- **Password Encryption**: BCrypt password hashing
- **CSRF Protection**: Built-in CSRF token validation
- **Session Management**: Secure session handling
- **Method-Level Security**: Controller endpoint protection

## Database Schema

### Core Entities
- **Users**: User accounts with roles and profiles
- **Projects**: Project containers for bugs with member assignments
- **Bugs**: Bug reports with complete lifecycle management
- **Comments**: Bug discussion threads with CRUD operations
- **Roles**: User permission system (ROLE_ADMIN, ROLE_USER)
- **Activity Logging**: System activity tracking

## Troubleshooting

### Common Issues

1. **Port 8080 already in use**:
   ```bash
   # Windows
   netstat -ano | findstr :8080
   taskkill /PID <PID> /F
   
   # Linux/Mac
   lsof -ti:8080 | xargs kill -9
   ```

2. **Database connection issues**:
   - Verify MySQL is running
   - Check credentials in `application-dev.yml`
   - Ensure database `bug_tracker_db` exists

3. **Template errors**:
   - Check Thymeleaf syntax
   - Verify model attributes are passed correctly
   - Use proper enum comparisons (`bug.status.name() == 'NEW'`)

4. **Build issues**:
   - Run `mvn clean compile` to rebuild
   - Ensure Java 21 is properly configured
   - Check Maven configuration

## Development Guidelines

1. **Code Quality**:
   - Follow Spring Boot best practices
   - Use proper logging (SLF4J) instead of `System.out.println()`
   - Implement proper error handling

2. **Template Development**:
   - Use semantic HTML with Bootstrap 5
   - Implement proper form validation
   - Follow Thymeleaf conventions

3. **Database**:
   - Use JPA annotations for entity relationships
   - Follow proper naming conventions
   - Implement cascade operations carefully

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
   - Improved performance when logging is disabled at certain levels
   - Lazy evaluation of parameters (calculated only if the log level is enabled)
   - Less impact on application performance in production

Example of proper logging in our codebase:
```java
// Incorrect approach
System.out.println("Created new bug with ID: " + bug.getId());

// Correct approach with SLF4J
log.info("Created new bug with ID: {}", bug.getId());
```

## Environment Variables

For production deployment, set these environment variables:

```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=bug_tracker_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# Admin Account
ADMIN_PASS=secure_admin_password

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
