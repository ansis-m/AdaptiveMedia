# AdaptiveMedia

Spring Boot application with PostgreSQL and Swagger.

## Prerequisites

- Java 21
- Docker Desktop
- IntelliJ IDEA (recommended for development)

## 🚀 Deployment

Run everything in containers:

### Build the application
```bash
./gradlew clean build
```

### Start all services (PostgreSQL + App) - initial run might take some time to download postgress image
```bash
docker-compose up --build -d
```


**Services:**
- Application: http://localhost:8080
- PostgreSQL: http://localhost:5432
- Swagger: http://localhost:8080/swagger-ui/index.html

**Stop deployment:**
```bash
docker-compose down
```

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

### Update Schema Flyway + JOOQ (db must be up and running): create flywheel script and then 
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
```
```bash
# View application logs
docker-compose logs app
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