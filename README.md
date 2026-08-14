# BuildTrack

**Production-ready REST API for managing software builds, validation issues and release workflows.**

BuildTrack is a backend portfolio project built with **Java 21 and Spring Boot**.  
It models the lifecycle of software builds from creation and validation to approval and release, with business rules, authentication, role-based authorization, database migrations, automated tests and production deployment.

## Live Demo

**API Landing Page**  
https://buildtrack-9fvd.onrender.com/

**Swagger UI**  
https://buildtrack-9fvd.onrender.com/swagger-ui/index.html

**Health Check**  
https://buildtrack-9fvd.onrender.com/actuator/health

> The application is hosted on Render's free tier, so the first request after a period of inactivity may take longer while the service starts.

---

## What does BuildTrack demonstrate?

BuildTrack was developed as a backend-focused project to apply production-oriented practices beyond basic CRUD operations.

The project includes:

- REST API design with Spring Boot
- Domain modeling and business rules
- Layered architecture
- PostgreSQL persistence with JPA / Hibernate
- Database versioning with Flyway
- JWT authentication
- Role-based authorization (`USER` / `ADMIN`)
- Pagination and filtering
- Centralized exception handling
- DTOs and mapping
- JPA auditing and domain timestamps
- Integration and controller testing
- Docker and Docker Compose
- Multiple environment profiles
- Health checks with Spring Boot Actuator
- OpenAPI / Swagger documentation
- CI/CD workflow
- Deployment with Render and Neon PostgreSQL

---

## Domain

BuildTrack revolves around three main concepts:

### Builds

A build represents a software version being validated before release.

Lifecycle:

```text
CREATED
   ↓
VALIDATING
   ├── APPROVED
   └── REJECTED
```

A build cannot be approved while it contains an unresolved `BLOCKER` issue.

Build versions use semantic versioning:

```text
major.minor.patch
```

Example:

```text
1.4.2
```

---

### Issues

Issues belong to builds and represent problems discovered during validation.

Each issue has:

- Severity
- Status
- Resolution timestamp

Lifecycle:

```text
OPEN → RESOLVED
```

Issue identifiers are globally unique.

---

### Releases

A release groups one or more builds.

Lifecycle:

```text
DRAFT
  ↓
READY
  ↓
PUBLISHED
```

A release can only become `READY` when:

- It contains at least one build
- Every build is approved
- No blocking validation problems remain

Published releases cannot be deleted.

---

## Authentication and Authorization

BuildTrack uses stateless authentication with **JWT Bearer tokens**.

### Public endpoints

```text
POST /auth/register
POST /auth/login

GET /
GET /actuator/health

Swagger / OpenAPI
```

### USER

Authenticated users can read:

```text
GET /builds/**
GET /releases/**
```

### ADMIN

Administrators can additionally create and modify builds, issues and releases.

Authorization is enforced by Spring Security.

---

## API Overview

Main resources:

```text
/auth
/builds
/releases
```

The complete API contract and request/response schemas are available through Swagger:

https://buildtrack-9fvd.onrender.com/swagger-ui/index.html

List endpoints support pagination and filtering.

Example:

```http
GET /builds?page=0&size=10&status=APPROVED&platform=WINDOWS
```

---

## Tech Stack

### Backend

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Spring Security
- Spring OAuth2 Resource Server
- Bean Validation

### Database

- PostgreSQL
- Hibernate / JPA
- Flyway

### API Documentation

- OpenAPI
- Swagger UI
- Springdoc

### Testing

- JUnit 5
- Spring Boot Test
- MockMvc
- Spring Security Test
- H2 for isolated tests
- PostgreSQL integration tests

### Infrastructure

- Maven
- Docker
- Docker Compose
- GitHub Actions
- Spring Boot Actuator
- Render
- Neon PostgreSQL

---

## Architecture

The application follows a layered backend architecture:

```text
HTTP Request
     │
     ▼
Controller
     │
     ▼
DTO / Mapper
     │
     ▼
Service
     │
     ▼
Domain Model
     │
     ▼
Repository
     │
     ▼
PostgreSQL
```

Responsibilities are separated between:

```text
controller
service
domain
repository
dto
mapper
config
exception
```

Business rules are kept inside the domain/service layer instead of being implemented directly in controllers.

---

## Database Migrations

Database schema changes are managed exclusively through **Flyway**.

Current migrations include:

```text
V1 - Initial schema
V2 - Database indexes
V3 - Technical auditing timestamps
V4 - Domain event timestamps
V5 - Application users and authentication
```

Hibernate runs with:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Therefore Hibernate validates the schema but does not create or modify production tables.

---

## Auditing

Entities include technical auditing timestamps:

```text
createdAt
updatedAt
```

The domain also records meaningful lifecycle events:

```text
Build.completedAt
Issue.resolvedAt
Release.publishedAt
```

This separates infrastructure auditing from domain events.

---

## Pagination and Filtering

Builds can be filtered by:

```text
status
platform
```

Releases can be filtered by:

```text
status
```

Responses use a common paginated structure containing:

```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

---

## Environment Configuration

BuildTrack separates configuration by environment:

```text
application.properties
application-dev.properties
application-docker.properties
application-prod.properties
```

Tests use their own configuration under:

```text
src/test/resources
```

Production credentials are supplied exclusively through environment variables.

Main production variables:

```text
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
DB_SSLMODE

JWT_SECRET
JWT_ISSUER
JWT_EXPIRATION_SECONDS

SPRING_PROFILES_ACTIVE
```

No production credentials are stored in the repository.

---

## Running Locally

### Requirements

- Java 21
- Maven
- Docker

Clone the repository:

```bash
git clone https://github.com/DoctoreJekyll/Buildtrack.git
cd Buildtrack/buildtrack-java
```

Start PostgreSQL:

```bash
docker compose up -d postgres
```

Run the API using the development profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Swagger will be available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Health check:

```text
http://localhost:8080/actuator/health
```

---

## Running with Docker

The complete application can also be started with:

```bash
docker compose up --build
```

This starts:

```text
BuildTrack API
      │
      ▼
PostgreSQL
```

using the Docker-specific Spring profile.

---

## Tests

Run the complete automated test suite with:

```bash
mvn clean test
```

The test strategy includes:

- Domain unit tests
- Service tests
- Repository tests
- Controller integration tests
- Authorization tests
- Real JWT authentication integration tests
- Pagination and filtering tests
- Flyway migration integration tests

Tests use H2 where database isolation is sufficient, while migration verification is performed against PostgreSQL.

---

## Production Deployment

Production architecture:

```text
                 Internet
                    │
                    ▼
              Render Web Service
                    │
              Docker container
                    │
             Spring Boot API
                    │
                    ▼
             Neon PostgreSQL
```

Render uses:

```text
SPRING_PROFILES_ACTIVE=prod
```

and monitors:

```text
GET /actuator/health
```

to verify application health.

Database communication uses SSL in production.

---

## Project Goals

BuildTrack was created to strengthen practical backend engineering skills while transitioning toward Java / Spring backend development.

Rather than focusing only on CRUD operations, the project emphasizes:

- Explicit domain rules
- Testable business logic
- Secure API access
- Database evolution
- Environment isolation
- Containerization
- Production deployment

The project is intentionally backend-only. Swagger UI acts as the interactive interface for exploring and testing the API.

---

## Author

**José Antonio Rodríguez**

Backend Developer — Java / Spring Boot

GitHub:  
https://github.com/DoctoreJekyll