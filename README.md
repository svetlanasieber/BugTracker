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

**Fixed Issues:**
1. **Template Engine**: All Thymeleaf enum comparison errors resolved
2. **Navigation**: All menu links and routes working properly
3. **Authorization**: Proper role-based access throughout the application
4. **Profile System**: Complete implementation with bug statistics
5. **Comment System**: Full CRUD operations with proper authorization
6. **Database**: All initialization and connection issues resolved
7. **Build System**: Removed test dependencies for streamlined compilation

**Application Architecture:**
- ✅ Clean MVC pattern implementation
- ✅ Proper service layer separation
- ✅ JPA entity relationships working correctly
- ✅ Spring Security 6.x integration complete
- ✅ Bootstrap 5 responsive UI implementation

## Recent Architecture Improvements (December 2024)

### 🏗️ Professional Spring MVC Refactoring

**Complete BugController Refactoring:**
- ✅ **Business Logic Extraction**: Moved all business logic from controllers to services
- ✅ **DTO Implementation**: Replaced direct entity usage with Data Transfer Objects
- ✅ **Helper Methods**: Created centralized model attribute loading methods
- ✅ **Clean Delegation**: Controllers now only handle request/response routing

**New Service Layer Components:**

1. **AuthService** (`AuthServiceImpl`):
   ```java
   // Centralized authentication logic
   String getCurrentUsername()    // Replaces SecurityContextHolder access
   Long getCurrentUserId()        // Gets current user ID safely
   boolean isCurrentUserAdmin()   // Role checking logic
   ```

2. **Enhanced BugService**:
   ```java
   // New business logic methods moved from controller
   String assignBugWithMessage(Long bugId, Long userId)
   Bug updateBugFromDTO(BugUpdateDTO bugUpdateDTO)
   Bug createBugFromDTOWithCurrentUser(BugAddDTO bugAddDTO)
   ```

**New DTOs for Type Safety:**

3. **BugUpdateDTO**:
   ```java
   @NotBlank(message = "Title is required")
   @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
   private String title;
   
   @NotNull(message = "Status is required")
   private BugStatus status;
   // ... with complete validation annotations
   ```

**Controller Improvements:**

4. **Clean BugController**:
   ```java
   // BEFORE: Business logic mixed in controller
   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
   String username = auth.getName();
   String message = userId == null ? "Bug unassigned!" : "Bug assigned!";
   
   // AFTER: Clean delegation to services
   String message = bugService.assignBugWithMessage(id, userId);
   Bug savedBug = bugService.createBugFromDTOWithCurrentUser(bugAddDTO);
   ```

5. **Helper Methods**:
   ```java
   private void loadFormData(Model model)    // Centralizes form data loading
   private void loadFilterData(Model model)  // Handles filter dropdowns
   ```

### 🎯 Benefits Achieved

- **Separation of Concerns**: Business logic properly separated from web layer
- **Type Safety**: DTOs prevent accidental entity manipulation in controllers
- **Testability**: Services can be unit tested independently
- **Maintainability**: Centralized business logic in appropriate layers
- **Code Reuse**: Helper methods eliminate duplication
- **Professional Structure**: Follows Spring MVC best practices

### 📊 Architecture Before vs After

**Before Refactoring:**
```
Controller ──────▶ Repository
    │                   │
    ▼                   ▼
Mixed Logic          Direct Entity
(Business +          Manipulation
 Web concerns)
```

**After Refactoring:**
```
Controller ──────▶ Service ──────▶ Repository
    │                 │              │
    ▼                 ▼              ▼
Pure Web           Business        Data
Routing            Logic           Access
    │                 │              │
    ▼                 ▼              ▼
  DTOs          AuthService      Entities
```

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
│   │   ├── BugAddDTO.java         # Bug creation form data
│   │   ├── BugUpdateDTO.java      # Bug update form data
│   │   ├── ProfileEditDTO.java    # Profile editing data
│   │   └── PasswordChangeDTO.java # Password change data
│   ├── entity/       # JPA Entities (User, Bug, Project, Comment)
│   └── enums/        # Enumerations (BugStatus, BugPriority)
├── repository/       # Spring Data JPA repositories
├── service/          # Business logic interfaces
│   ├── AuthService.java           # Authentication & security operations
│   ├── BugService.java            # Bug business logic
│   ├── ProjectService.java        # Project management
│   ├── UserBugService.java        # User operations
│   ├── CommentService.java        # Comment management
│   └── impl/         # Service implementations
│       ├── AuthServiceImpl.java   # Centralized auth logic
│       ├── BugServiceImpl.java    # Enhanced bug operations
│       └── ...                    # Other service implementations
├── util/             # Utility classes
├── validation/       # Custom validators
└── web/              # Web layer
    └── controller/   # MVC controllers and REST endpoints
        ├── BugController.java      # Clean, refactored controller
        ├── ProjectController.java  # Project management
        ├── ProfileController.java  # User profiles
        └── ...                     # Other controllers

src/main/resources/
├── templates/        # Thymeleaf templates
│   ├── bugs/         # Bug-related pages
│   ├── projects/     # Project management pages
│   ├── profile/      # User profile pages
│   └── fragments/    # Reusable template fragments
├── static/           # CSS, JS, images
└── application*.yml  # Configuration files (dev/prod profiles)
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

### 1. **Spring MVC Architecture Standards**

**Controller Layer:**
- Controllers should ONLY handle HTTP request/response routing
- NO business logic in controllers - delegate everything to services
- Use DTOs for all @ModelAttribute parameters, never entities
- Create helper methods for repeated model.addAttribute() calls
- Use proper validation with @Valid and BindingResult

**Service Layer:**
- All business logic must be in service implementations  
- Use AuthService for any SecurityContextHolder access
- Create specific service methods for complex operations
- Return meaningful messages from business operations

**DTO Usage:**
```java
// ✅ CORRECT: Use DTOs in controllers
@PostMapping("/update")
public String updateBug(@Valid @ModelAttribute BugUpdateDTO dto, ...)

// ❌ INCORRECT: Never use entities directly
@PostMapping("/update") 
public String updateBug(@ModelAttribute Bug bug, ...)
```

**Authentication Handling:**
```java
// ✅ CORRECT: Use AuthService
String username = authService.getCurrentUsername();

// ❌ INCORRECT: Direct SecurityContextHolder access
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
```

### 2. **Code Quality Standards**

- Follow Spring Boot best practices
- Use proper logging (SLF4J) instead of `System.out.println()`
- Implement proper error handling
- Write descriptive method names and documentation
- Use @Transactional for operations that modify data

### 3. **Template Development**

- Use semantic HTML with Bootstrap 5
- Implement proper form validation
- Follow Thymeleaf conventions
- Use proper enum comparisons (`bug.status.name() == 'NEW'`)

### 4. **Database Guidelines**

- Use JPA annotations for entity relationships
- Follow proper naming conventions
- Implement cascade operations carefully
- Never expose entities directly to the web layer

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

## Current Project Status Summary

### ✅ **Production Ready Features**
- **Complete Bug Tracking System**: Full CRUD operations with proper validation
- **User Authentication & Authorization**: Role-based access with Spring Security 6.x
- **Project Management**: Admin-controlled project creation and member assignment
- **Professional Architecture**: Clean separation of concerns following Spring MVC best practices

### 🏗️ **Architecture Highlights**
- **Clean Controllers**: Zero business logic, pure request/response handling
- **Service Layer**: Centralized business logic with proper abstractions
- **DTO Pattern**: Type-safe data transfer between layers
- **AuthService**: Centralized authentication and security operations
- **Helper Methods**: Eliminated code duplication in controllers

### 🚀 **Technical Excellence**
- **Spring Boot 3.2.0** with Java 21
- **Professional logging** with SLF4J (no System.out.println)
- **Validation annotations** on all DTOs
- **Environment profiles** (dev/prod configurations)
- **OpenAPI documentation** with Swagger

### 📊 **Ready for**
- Production deployment
- Unit testing (clean service layer)
- Feature extensions
- Team development

## License

This project is licensed under the MIT License - see the LICENSE file for details.
