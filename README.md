─────────────────────────────────────┐    ┌─────────────────────────────────────┐
│          MAIN APPLICATION            │    │        PROJECT MICROSERVICE         │
│         (Bug Tracker)                │    │        (Project Service)            │
│         Port: 8080                   │    │         Port: 8081                  │
├─────────────────────────────────────┤    ├─────────────────────────────────────┤
│ • Users, Bugs, Comments              │    │ • Projects CRUD                     │
│ • Authentication & Security          │    │ • Project Management                │
│ • Thymeleaf UI                       │    │ • REST API                          │
│ • File Uploads                       │◄──►│ • Independent Database              │
│ • Feign Client                       │    │ • Validation & Error Handling       │
├─────────────────────────────────────┤    ├─────────────────────────────────────┤
│     Database: bug_tracker_main_db    │    │   Database: project_service_db      │
└─────────────────────────────────────┘    └─────────────────────────────────────┘
