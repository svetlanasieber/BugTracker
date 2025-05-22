# Bug Tracker Application - Technical Documentation

## 1. Project Overview

### Purpose
The Bug Tracker is a comprehensive web application designed to help development teams track, manage, and resolve software bugs throughout the development lifecycle. It provides functionality for creating projects, reporting bugs, assigning them to team members, tracking their status, and facilitating communication through comments.

### Architecture
The application follows the Model-View-Controller (MVC) architectural pattern:
- **Model**: Represents data structures and business logic
- **View**: Thymeleaf templates rendering the UI
- **Controller**: Handles HTTP requests and mediates between Model and View

### Technology Stack
- **Backend**: Spring Boot 3.x
- **Frontend**: Thymeleaf, Bootstrap 5, jQuery
- **Database**: MySQL 8.x
- **Security**: Spring Security 6.x
- **Build Tool**: Maven

## 2. Package Structure

### `model` Package
Contains entity classes and Data Transfer Objects (DTOs):
- `model.entity`: Domain model classes mapped to database tables
- `model.dto`: Data Transfer Objects for view-specific data structures

### `repository` Package
Contains interfaces extending Spring Data JPA repositories:
- Provides data access layer with methods for CRUD operations
- Includes custom query methods using method naming conventions or `@Query` annotations

### `service` Package
Contains business logic implementation:
- Service interfaces defining operations
- Implementation classes in the `service.impl` subpackage
- Transaction management with `@Transactional` annotations

### `web.controller` Package
Contains Spring MVC controllers:
- `AdminController`: Admin-specific functionality
- `BugController`: Bug creation and management
- `CommentController`: Comment functionality
- `ProjectController`: Project management
- `UserController`: User profile and settings

### `config` Package
Contains configuration classes:
- `SecurityConfig`: Spring Security configuration
- `InitialDataConfig`: Initial data setup
- `WebConfig`: Web-related configuration

### `validation` Package
Contains custom validation logic:
- Custom constraints and validators
- Annotations for declarative validation

### `exception` Package
Contains exception handling:
- Custom exceptions
- Global exception handler

### `scheduler` Package
Contains scheduled tasks:
- `BugAutoCloseScheduler`: Automatically closes stale bugs

## 3. Entity Model

### Core Entities

#### `User`
- Represents application users with different roles
- Fields: id, firstName, lastName, email, password, isActive, createdAt, updatedAt
- Relationships:
  - Many-to-Many with `Role` (via `users_roles` table)
  - One-to-Many with `Bug` (reporter)
  - One-to-Many with `Bug` (assignee)
  - Many-to-Many with `Project` (via `project_members` table)

#### `Role`
- Represents user roles (ADMIN, USER)
- Fields: id, name
- Relationships:
  - Many-to-Many with `User`

#### `Project`
- Represents development projects
- Fields: id, name, description, startDate, endDate, isActive, createdAt, updatedAt
- Relationships:
  - One-to-Many with `Bug`
  - Many-to-Many with `User` (team members)

#### `Bug`
- Central entity representing reported issues
- Fields: id, title, description, stepsToReproduce, status (enum), priority (enum), createdAt, updatedAt, closedAt
- Relationships:
  - Many-to-One with `Project`
  - Many-to-One with `User` (reporter)
  - Many-to-One with `User` (assignee)
  - One-to-Many with `Comment`

#### `Comment`
- Represents comments on bugs
- Fields: id, content, createdAt, updatedAt
- Relationships:
  - Many-to-One with `Bug`
  - Many-to-One with `User` (author)

#### `LogEntry`
- Captures system events for auditing
- Fields: id, action, entityType, entityId, userId, details, level, createdAt

### Enums
- `Bug.BugStatus`: NEW, IN_PROGRESS, TESTING, RESOLVED, CLOSED
- `Bug.BugPriority`: LOW, MEDIUM, HIGH, CRITICAL

## 4. Spring Security Configuration

The application uses Spring Security for authentication and authorization:

### Authentication
- Form-based authentication with email and password
- Custom `UserDetailsService` implementation for loading user details
- BCrypt password encoding

### Authorization
- Role-based access control with ADMIN and USER roles
- Method-level security with `@PreAuthorize` annotations
- URL-based security rules in `SecurityConfig`

### Key Security Components
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    // Security filter chain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        // Configures URL patterns, login/logout, and CSRF protection
    }
    
    // Admin initialization on application startup
    @Bean
    public CommandLineRunner initializeAdmin() {
        // Creates default admin user if none exists
    }
}
```

## 5. Validation

### Custom Annotations
- `@ValidEmail`: Validates email format
- `@ValidBugStatus`: Validates bug status transitions

### Validators
- `EmailValidator`: Implements email validation logic
- `BugStatusValidator`: Validates bug status transitions based on current status

### Error Handling
- `GlobalExceptionHandler`: Centralized exception handling
- Custom exception classes (e.g., `UserAlreadyExistsException`, `BugNotFoundException`)
- Custom error views for different HTTP status codes

## 6. Test Coverage

### Unit Tests
- Service layer tests with Mockito
- Repository tests with test database
- Validator tests for custom validation

### Controller Tests
- Web MVC tests with MockMvc
- Security tests with Spring Security Test
- Form submission tests

### Integration Tests
- End-to-end tests with TestRestTemplate
- Database integration tests with @DataJpaTest
- Security integration tests

### Test Coverage Statistics
- Overall test coverage: ~70%
- Service layer coverage: ~80%
- Controller layer coverage: ~75%
- Repository layer coverage: ~90%

## 7. Database Schema

### Main Tables
- `users`: Stores user information
- `roles`: Stores available roles
- `users_roles`: Junction table for user-role relationship
- `projects`: Stores project information
- `project_members`: Junction table for project-user relationship
- `bugs`: Stores bug information
- `comments`: Stores comments on bugs
- `log_entries`: Stores system events for auditing

### Key Relationships
- One user can belong to multiple projects
- One project can have multiple team members
- One project can have multiple bugs
- One bug can have multiple comments
- Each bug has one reporter and optionally one assignee

### Schema Creation
The schema is created using Flyway migrations:
- `V1__Initial_Schema.sql`: Creates initial tables and relationships
- `V2__Insert_Default_Data.sql`: Inserts default roles and admin user

## 8. Special Endpoints and Utilities

### Admin Endpoints
- `/admin/users`: User management
- `/admin/reset-admin`: Resets admin account
- `/admin/debug-info`: Shows system debug information

### Utility Endpoints
- `/fix-database`: Script to fix database consistency issues
- `/fix-admin-roles`: Script to fix admin role issues
- `/projects/fix-projects`: Script to ensure projects exist

### Database Fix Scripts
- `fix_db.sql`: Comprehensive database fix script
- `sample_project.sql`: Creates sample projects for testing

### Error Pages
- `/error/access-denied.html`: Custom access denied page
- `/error/404.html`: Custom not found page

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Maven

### Database Setup
1. Create a MySQL database named `bug_tracker_db`
2. Update database credentials in `application.yml` if needed

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build the project
4. Run `mvn spring-boot:run` to start the application
5. Access the application at http://localhost:8080

### Default Login
- Admin: admin@bugtracker.com / password: admin123
- Create new user accounts via the registration page

## Project Structure
```
com.bugtracker.bugtracker
│
├── config
│   ├── SecurityConfig.java
│   ├── InitialDataConfig.java
│   └── CustomUserDetailsService.java
│
├── model
│   ├── entity
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Bug.java
│   │   ├── Project.java
│   │   ├── Comment.java
│   │   └── LogEntry.java
│   │
│   └── dto
│       ├── UserRegisterDTO.java
│       ├── UserLoginDTO.java
│       ├── BugAddDTO.java
│       └── (Other DTOs)
│
├── repository
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── BugRepository.java
│   ├── ProjectRepository.java
│   ├── CommentRepository.java
│   └── LogEntryRepository.java
│
├── service
│   ├── impl
│   │   ├── UserServiceImpl.java
│   │   ├── BugServiceImpl.java
│   │   ├── ProjectServiceImpl.java
│   │   ├── CommentServiceImpl.java
│   │   └── LogServiceImpl.java
│   │
│   ├── UserService.java
│   ├── BugService.java
│   ├── ProjectService.java
│   ├── CommentService.java
│   └── LogService.java
│
├── validation
│   ├── ValidEmail.java
│   ├── EmailValidator.java
│   ├── ValidBugStatus.java
│   └── BugStatusValidator.java
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── UserAlreadyExistsException.java
│   ├── BugNotFoundException.java
│   └── ProjectNotFoundException.java
│
├── scheduler
│   └── BugAutoCloseScheduler.java
│
├── web
│   ├── controller
│   │   ├── UserController.java
│   │   ├── AdminController.java
│   │   ├── BugController.java
│   │   ├── ProjectController.java
│   │   ├── CommentController.java
│   │   ├── ErrorController.java
│   │   └── FixController.java
│
└── resources
    ├── templates
    │   ├── auth
    │   │   ├── login.html
    │   │   └── register.html
    │   ├── admin
    │   │   └── users.html
    │   ├── bugs
    │   │   ├── list.html
    │   │   ├── details.html
    │   │   └── create.html
    │   ├── projects
    │   │   ├── list.html
    │   │   ├── view.html
    │   │   ├── create.html
    │   │   └── edit.html
    │   ├── comments
    │   │   └── edit.html
    │   ├── error
    │   │   └── access-denied.html
    │   ├── dashboard.html
    │   └── landing.html
    │
    ├── static
    │   ├── css
    │   ├── js
    │   └── images
    │
    └── db/migration
        ├── V1__Initial_Schema.sql
        └── V2__Insert_Default_Data.sql
```

## License
This project is for educational purposes as part of a diploma project.
