# BuildTrack

![CI](https://github.com/DoctoreJekyll/Buildtrack/actions/workflows/ci.yml/badge.svg)

BuildTrack is a backend API for managing software builds, validation issues, and release readiness.

The project models a simplified release workflow where builds are created, validated, approved or rejected, linked to issues, and grouped into releases before publication.

## Project Purpose

BuildTrack was built as a Java backend portfolio project focused on realistic backend development practices:

* Domain-driven business rules
* REST API design
* DTO-based request and response models
* Validation and centralized error handling
* Relational persistence with JPA
* PostgreSQL database integration
* Dockerized local environment
* Automated testing with GitHub Actions
* Docker image publication through CI

The goal is to demonstrate a backend service that goes beyond basic CRUD by including business transitions, validation rules, persistence relationships, and automated quality checks.

## Main Features

### Builds

A build represents a software build candidate for a specific platform.

Supported build lifecycle:

```text
CREATED → VALIDATING → APPROVED
CREATED → VALIDATING → REJECTED
```

Main rules:

* A build starts in `CREATED` status.
* A build can only move to `VALIDATING` from `CREATED`.
* A build can only be approved or rejected from `VALIDATING`.
* A build cannot be approved if it has open blocker issues.

### Issues

Issues represent validation problems found in a build.

Supported issue lifecycle:

```text
OPEN → RESOLVED
```

Main rules:

* An issue starts in `OPEN` status.
* Only open issues can be resolved.
* A build cannot contain duplicated issue IDs.
* A build with an open `BLOCKER` issue cannot be approved.

### Releases

A release groups approved builds and represents a publishable software release.

Supported release lifecycle:

```text
DRAFT → READY → PUBLISHED
```

Main rules:

* A release starts in `DRAFT` status.
* Builds can only be added to a `DRAFT` release.
* A release cannot be prepared without builds.
* All builds must be approved before preparing or publishing a release.
* A release cannot be prepared or published if any related build has open blocker issues.

## Tech Stack

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Jakarta Validation
* PostgreSQL
* H2 Database for tests
* Maven
* Docker
* Docker Compose
* GitHub Actions
* Docker Hub

## Architecture Overview

The project follows a layered backend structure:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Domain / Persistence Entities
```

Main packages:

```text
controller   → REST endpoints
service      → application use cases
domain       → business entities and rules
repository   → Spring Data JPA repositories
dto          → request and response models
mapper       → domain-to-DTO mapping
exceptions   → custom exceptions and global error handling
```

## Persistence Model

The application uses PostgreSQL in development and Docker environments.

Main persisted concepts:

```text
Build
BuildVersion
Issue
Release
```

Relationships:

```text
Build 1 ---- N Issue
Release N ---- N Build
```

Database tables include:

```text
builds
issues
releases
release_builds
```

`release_builds` is the join table used to persist the many-to-many relationship between releases and builds.

## Running the Project with Docker

The easiest way to run the full application is with Docker Compose.

From the project folder:

```bash
cd buildtrack-java
docker compose up --build
```

This starts:

```text
buildtrack-api
buildtrack-postgres
```

The API will be available at:

```text
http://localhost:8080
```

PostgreSQL runs inside Docker and is configured through the `docker` Spring profile.

To stop the containers:

```bash
docker compose down
```

To stop the containers and remove the database volume:

```bash
docker compose down -v
```

Use `-v` only when you want to delete the local PostgreSQL data.

## Running the Application Locally

You can also run the Spring Boot application locally while using PostgreSQL from Docker.

Start PostgreSQL:

```bash
cd buildtrack-java
docker compose up -d postgres
```

Run the app with the `dev` profile:

```bash
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

The API will be available at:

```text
http://localhost:8080
```

## Running Tests

Tests use H2 in-memory database by default.

From the project folder:

```bash
cd buildtrack-java
mvn clean test
```

The test suite covers:

* Domain rules
* Service use cases
* Controller/API flows with MockMvc
* Build lifecycle
* Issue lifecycle
* Release lifecycle
* Error handling scenarios

## CI/CD

The project uses GitHub Actions for continuous integration.

On every push or pull request to `main`, the pipeline:

1. Checks out the repository.
2. Sets up Java 21.
3. Runs Maven tests.
4. Builds the Docker image.
5. Publishes the Docker image to Docker Hub on push events.

Docker image:

```text
joseajierro/buildtrack-api:latest
```

## Example API Flow

### Create a build

```http
POST /builds
```

```json
{
  "id": "B-001",
  "version": "1.0.0",
  "platform": "WINDOWS"
}
```

### Add an issue to the build

```http
POST /builds/B-001/issues
```

```json
{
  "id": "ISSUE-001",
  "title": "Crash on startup",
  "severity": "BLOCKER"
}
```

### Resolve the issue

```http
POST /builds/B-001/issues/ISSUE-001/resolve
```

### Start build validation

```http
POST /builds/B-001/validate
```

### Approve the build

```http
POST /builds/B-001/approve
```

### Create a release

```http
POST /releases
```

```json
{
  "id": "R-001",
  "name": "Release 1.0.0"
}
```

### Add the build to the release

```http
POST /releases/R-001/builds/B-001
```

### Prepare the release

```http
POST /releases/R-001/prepare
```

### Publish the release

```http
POST /releases/R-001/publish
```

## Current Status

Implemented:

* Build lifecycle management
* Issue management
* Release workflow
* REST API endpoints
* DTO mapping
* Validation
* Centralized error responses
* JPA persistence
* PostgreSQL integration
* Docker Compose setup
* GitHub Actions CI
* Docker Hub image publication

## Possible Next Improvements

Planned or potential improvements:

* OpenAPI/Swagger documentation
* Pagination and filtering
* Flyway database migrations
* Authentication and authorization
* More detailed integration tests
* Docker image security hardening
* Improved Docker image tagging strategy
* Deployment to a cloud platform
