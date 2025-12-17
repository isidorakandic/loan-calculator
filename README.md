# Loan Calculator

A Spring Boot service that calculates amortized loan schedules. It exposes REST endpoints for creating loan requests and retrieving their calculated monthly installments, persisting everything in MySQL for later lookup.

## Quick Overview
- **What it does:** Accepts a loan amount, interest rate, and term in months, calculates monthly payments with banker’s rounding, and stores the resulting schedule.
- **API surface:**
  - `POST /loans` — create a loan and return its amortization schedule.
  - `GET /loans` — list all stored loans with pagination.
- **Port mapping:** The app runs on port `8080` inside the container and is exposed as `8088` when using Docker Compose.

## Architecture
The service follows a typical Spring layered architecture:
- **Controller** (`LoanRequestController`) — defines the REST endpoints for creating and listing loans.
- **Service** (`LoanService`) — contains the amortization calculation logic and orchestrates persistence.
- **Mapper** (`LoanRequestMapper`) — converts between incoming DTOs, entities, and response DTOs using MapStruct.
- **Repository** (`LoanRequestRepository`) — Spring Data JPA repository for database access.
- **Entities** (`LoanRequest`, `Installment`) — JPA models persisted to MySQL.
- **DTOs** (`CreateLoanRequestDTO`, `LoanResponseDTO`, `InstallmentDTO`) — request/response contracts with validation annotations for input safety.

## Tools & Technologies
- **Java 25** — programming language and runtime.
- **Maven** — build tool and dependency management.
- **Spring Boot 4 (Web, Data JPA)** — application framework for REST APIs and database access.
- **Spring Validation (Jakarta Validation)** — request validation for DTOs.
- **Springdoc OpenAPI** — auto-generated API docs and Swagger UI.
- **Spring Boot Actuator** — health and metrics endpoints.
- **MapStruct** — compile-time DTO/entity mapping.
- **Lombok** — boilerplate reduction (getters/setters/constructors).
- **MySQL** — primary relational database.
- **H2** — lightweight in-memory database for tests.
- **JUnit & Spring Test** — unit and integration testing support.
- **Docker & Docker Compose** — containerization and multi-service orchestration.

## Getting Started
### Clone the repository
```bash
git clone https://github.com/your-org/loan-calculator.git
cd loan-calculator
```

### Build the application
Use Maven (the repo includes the Maven Wrapper):
```bash
./mvnw clean package -DskipTests
```
The build produces `target/loan-calculator-0.0.1-SNAPSHOT.jar` consumed by the Docker image.

### Run with Docker Compose
1. Ensure Docker and Docker Compose are installed.
2. Start the application and MySQL:
   ```bash
   docker compose up --build
   ```
   This builds the app image, starts MySQL with the `loan_calculator` database, and launches the API on port `8088`.
3. Stop and remove the containers:
   ```bash
   docker compose down
   ```

### Environment
- Default MySQL credentials are `root`/`root` (set in `docker-compose.yml`).
- The application uses the datasource URL `jdbc:mysql://mysql:3306/loan_calculator` when running via Docker Compose.

### Useful Endpoints
- Swagger UI: `http://localhost:8088/swagger-ui/index.html`
- Health check: `http://localhost:8088/actuator/health`
- Loans API: `http://localhost:8088/loans`

### Logs
Application logs are written to `logs/loan-calculator.log` inside the container.

## Project Layout
- `src/main/java/com/loan_calculator/` — application code (controllers, services, mappers, entities, repositories, DTOs).
- `src/main/resources/application.properties` — Spring Boot configuration.
- `docker-compose.yml` — local runtime stack (app + MySQL).
- `Dockerfile` — builds the application container image.
- `docker-entrypoint-initdb.d` — optional MySQL initialization scripts.
- `pom.xml` — dependencies and Maven build configuration.

## Contributing
1. Create a feature branch.
2. Add tests where appropriate.
3. Run `./mvnw test` before opening a pull request.
