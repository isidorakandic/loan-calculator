# Loan Calculator

A Spring Boot service that calculates amortized loan schedules. It exposes REST endpoints for creating loan requests and
retrieving their calculated monthly installments, persisting everything in MySQL for later lookup.

## Quick Overview

- **What it does:** Accepts a loan amount, interest rate, and term in months, calculates monthly payments with banker’s
  rounding, and stores the resulting schedule.
- **API surface:**
    - `POST /loans` — create a loan and return its amortization schedule.
    - `GET /loans` — list all stored loans with pagination.

## Architecture

The service follows a typical Spring layered architecture:

- **Controller** (`LoanRequestController`) — defines the REST endpoints for creating and listing loans.
- **Service** (`LoanService`) — contains the amortization calculation logic and orchestrates persistence.
- **Mapper** (`LoanRequestMapper`) — converts between incoming DTOs, entities, and response DTOs using MapStruct.
- **Repository** (`LoanRequestRepository`) — Spring Data JPA repository for database access.
- **Entities** (`LoanRequest`, `Installment`) — JPA models persisted to MySQL.
- **DTOs** (`CreateLoanRequestDTO`, `LoanResponseDTO`, `InstallmentDTO`) — request/response contracts with validation
  annotations for input safety.

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

## Run the app in Docker container

1. Clone the repository:
    ```bash
    git clone https://github.com/your-org/loan-calculator.git
    cd loan-calculator
    ```

2. Start the application and MySQL in Docker by running:
   ```bash
   docker compose up --build
   ```
   Mock data is initialized in the DB for better testing :)


4. Stop and remove the containers:
   ```bash
   docker compose down
   ```

## Useful Endpoints

- Swagger UI: `http://localhost:8088/swagger-ui/index.html`
- Health check: `http://localhost:8088/actuator/health`
- Loans API: `http://localhost:8088/loans`

### Logs

Application logs are written to `logs/loan-calculator.log` inside the container.


