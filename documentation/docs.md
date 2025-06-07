# Bug Tracker - Software Issue Tracking System

## Overview

Bug Tracker is a web-based system for tracking and managing software issues (bugs). The system enables teams to report, track, and resolve software issues in an organized and efficient manner.

## Implemented Features

### 1. User Management
- User registration with email and password
- Authentication using Spring Security
- Roles: Administrator and User with different permissions
- Profile pages with basic information
- Personal information and password modification

### 2. Project Management
- Project creation (admin only)
- Adding and removing project members
- Viewing all accessible projects
- Detailed project information

### 3. Bug Management
- Bug creation and editing
- Priority and status categorization
- Bug assignment to developers
- Bug commenting
- Automatic closure of inactive bugs after 30 days

## Technical Implementation

### 1. Backend Technologies
- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL Database
- Flyway Migrations
- Maven

### 2. Frontend Technologies
- Thymeleaf templates
- Bootstrap 5.3.2
- Responsive design

### 3. Database
- MySQL 8.0
- Flyway version control
- Relational model with referential integrity

### 4. Architecture
- MVC architectural pattern
- Separation of concerns
- Repository and Service layers
- Dependency Injection

## Security

### Implemented Measures
- BCrypt password hashing
- Spring Security integration
- CSRF protection
- Input validation
- Role-based access control

## Automation

### Processes
- Automatic closure of inactive bugs
- System action logging
- Automatic database migrations

## Planned Future Improvements

### Short-term
1. Email notification implementation
2. Enhanced statistics and reporting
3. REST API with JWT authentication

### Long-term
1. External system integration
2. Mobile application
3. Extended customization options

## System Requirements

### Server
- Java 17+
- MySQL 8.0+
- 2GB RAM minimum
- 10GB disk space

### Client
- Modern web browser
- JavaScript enabled
- Minimum resolution: 1024x768 
