# Database Documentation

## Overview

The Bug Tracker system uses a MySQL 8.0+ database with Flyway migrations for version control. The database schema follows a relational model with proper foreign key constraints and referential integrity.

## Tables Structure

### Users
Stores user account information.
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);
```
- `id`: Unique identifier
- `first_name`: User's first name
- `last_name`: User's last name
- `email`: Unique email address for login
- `password`: BCrypt hashed password
- `created_at`: Account creation timestamp
- `updated_at`: Last update timestamp
- `is_active`: Account status flag

### Roles
Defines system roles for authorization.
```sql
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
```
- `id`: Unique identifier
- `name`: Role name (e.g., 'ROLE_ADMIN', 'ROLE_USER')

### Users_Roles
Junction table for many-to-many relationship between users and roles.
```sql
CREATE TABLE users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
```
- Composite primary key of `user_id` and `role_id`
- Foreign key constraints ensure data integrity

### Projects
Stores project information.
```sql
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```
- `id`: Unique identifier
- `name`: Project name
- `description`: Detailed project description
- `start_date`: Project start date
- `end_date`: Project end date
- `is_active`: Project status flag
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp

### Project_Members
Junction table for project membership.
```sql
CREATE TABLE project_members (
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (project_id, user_id),
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
- Manages project membership
- Composite primary key ensures unique memberships
- Foreign key constraints maintain referential integrity

### Bugs
Main table for bug tracking.
```sql
CREATE TABLE bugs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    project_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    assigned_to_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (assigned_to_id) REFERENCES users(id)
);
```
- `id`: Unique identifier
- `title`: Bug title
- `description`: Detailed bug description
- `status`: Current status (e.g., NEW, IN_PROGRESS, RESOLVED)
- `priority`: Bug priority level
- `project_id`: Associated project
- `reporter_id`: User who reported the bug
- `assigned_to_id`: User assigned to fix the bug
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp
- `closed_at`: Resolution timestamp

### Comments
Stores bug comments and discussions.
```sql
CREATE TABLE comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    bug_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (bug_id) REFERENCES bugs(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);
```
- `id`: Unique identifier
- `content`: Comment text
- `bug_id`: Associated bug
- `author_id`: Comment author
- `created_at`: Creation timestamp
- `updated_at`: Last update timestamp

### Log_Entries
System activity logging table.
```sql
CREATE TABLE log_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    user_id BIGINT,
    details TEXT,
    level VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```
- `id`: Unique identifier
- `action`: Type of action performed
- `entity_type`: Type of entity affected
- `entity_id`: ID of affected entity
- `user_id`: User who performed the action
- `details`: Additional information
- `level`: Log level (e.g., INFO, WARNING, ERROR)
- `created_at`: Event timestamp

## Entity Relationships

1. **User - Role** (Many-to-Many)
   - Users can have multiple roles
   - Roles can be assigned to multiple users
   - Implemented through `users_roles` junction table

2. **Project - User** (Many-to-Many)
   - Projects can have multiple members
   - Users can be members of multiple projects
   - Implemented through `project_members` junction table

3. **Bug - Project** (Many-to-One)
   - Each bug belongs to one project
   - Projects can have multiple bugs

4. **Bug - User** (Multiple relationships)
   - Reporter (Many-to-One): Each bug has one reporter
   - Assignee (Many-to-One): Each bug can be assigned to one user
   - Users can report and be assigned to multiple bugs

5. **Comment - Bug** (Many-to-One)
   - Each comment belongs to one bug
   - Bugs can have multiple comments

6. **Comment - User** (Many-to-One)
   - Each comment has one author
   - Users can create multiple comments

## Database Maintenance

### Automated Processes
- Flyway handles database migrations
- Automatic timestamp updates on record modification
- System logging of all significant actions

### Data Integrity
- Foreign key constraints prevent orphaned records
- Unique constraints prevent duplicate entries
- Default values ensure data consistency

### Performance Considerations
- Indexes on frequently queried columns
- Timestamp fields for tracking and auditing
- Appropriate field types and sizes 
