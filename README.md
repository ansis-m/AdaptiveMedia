# AdaptiveMedia

Spring Boot application with PostgreSQL, JOOQ and Swagger.

## Prerequisites

- Docker Desktop - ideally should pull postgres:16-alpine beforehand
- Java 21 (only needed for local development)
- IntelliJ IDEA (recommended for development)

## 🚀 Deployment
Before deployment **juno** is required to be up and running

### Option 1: Using Pre-built Docker Image (Recommended)

The simplest way to run the application - uses a pre-built image from Docker Hub:

```bash
# Start all services (PostgreSQL + App)
docker-compose -f docker-compose-prebuilt.yml up -d
```

**Stop deployment:**
```bash
docker-compose -f docker-compose-prebuilt.yml down
```

This approach ensures consistent cross-platform compatibility (Windows/Mac/Linux) with no build step required.

### Option 2: Build Locally

If you prefer to build the application locally:

#### Build the application
```bash
./gradlew clean build
```

#### Start all services (PostgreSQL + App) - initial run might take some time to download postgres image
```bash
docker-compose up --build -d
```

**Stop deployment:**
```bash
docker-compose down
```

### Access Points

**Services:**
- Application: http://localhost:8080
- PostgreSQL: http://localhost:5432
- Swagger: http://localhost:8080/swagger-ui/index.html

## 🛠️ Development

Run services in containers, app locally in IntelliJ:

### 1. Start Services
```bash
# Start only PostgreSQL
docker-compose up postgres -d
```

```bash
# Stop the app container if it's running
docker stop adaptivemedia-app
```

### 2. Run Application Locally
- Open project in IntelliJ IDEA
- Run the main application class
- App connects to containerized services on localhost

## 🔄 Database Schema Management

### Update Schema Flyway + JOOQ (postgress must be up and running): create flywheel .sql script and then
```bash
./gradlew updateSchema
```

This task automatically:
1. **Runs Flyway migrations** - Applies all pending database migrations from `src/main/resources/db/migration`
2. **Generates JOOQ code** - Creates type-safe Java classes for database tables in `src/main/java/generated`

### Generated code is committed with sources - this circumvents chicken-and-egg problem

Run this whenever you add new migration files or need to sync your JOOQ classes with database changes.

## 🐳 Docker Commands

```bash
# View running containers
docker-compose ps
# or for pre-built version
docker-compose -f docker-compose-prebuilt.yml ps
```
```bash
# View application logs
docker-compose logs app
# or for pre-built version
docker-compose -f docker-compose-prebuilt.yml logs app
```
```bash
# View service logs
docker-compose logs postgres
```

## 🩺 Health Checks

- Application health: http://localhost:8080/actuator/health
- PostgreSQL: Connect with any DB client to `localhost:5432`

## 📝 Database

- **Host:** localhost:5432
- **Database:** adaptivemedia
- **Username:** adaptivemedia
- **Password:** adaptivemedia

## 📦 Docker Hub Image

The application image is publicly available on Docker Hub as `ansism/adaptivemedia-app:latest`