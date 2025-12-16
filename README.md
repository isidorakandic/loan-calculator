# Loan Calculator

This project is a Spring Boot-based loan calculator that keeps controllers thin, delegates business rules to a service layer, and persists requests/responses through a repository abstraction. The included UML artifacts (in XMI format) capture the recommended architecture for the application.

## Business Logic
- Accept loan calculation requests containing principal, annual interest rate, and term in months.
- Compute the monthly installment using a standard amortization formula and derive totals (e.g., total interest paid across the term).
- Persist the calculation request and derived schedule so that clients can retrieve the history of prior computations.
- Keep controllers focused on request validation and response mapping while the service encapsulates financial calculations and repository calls.

## Architecture and Design Approach
- **Layered design:** A `LoanCalculatorController` receives HTTP requests and delegates to `LoanCalculationService`. The service performs validation and amortization math, then coordinates with a `LoanRepository` for persistence.
- **DTOs and domain separation:** Requests and schedules are captured as DTOs (`LoanRequest`, `LoanSchedule`) while `Loan` models the persisted entity.
- **Clean code defaults:** Constructors or Lombok-generated methods keep classes minimal, and MapStruct can map between DTOs and the entity without manual boilerplate.
- **Observability and openness:** Actuator endpoints support basic health and metrics exposure. Springdoc/OpenAPI generates HTTP contracts for consumers.
- **Extensibility:** The repository abstraction allows switching between in-memory, JDBC, or JPA-backed implementations without changing controllers.

## Diagrams (XMI and image formats)
- **Class diagram (image):** `docs/diagrams/class-diagram.svg` provides a human-readable picture of the controller-service-repository flow, domain objects (`Loan`, `LoanRequest`, `LoanSchedule`), and their primary operations and relationships.
- **Sequence diagram (image):** `docs/diagrams/sequence-diagram.svg` illustrates the synchronous request cycle from client to controller, through service and repository, and back with a calculated schedule.
- **XMI sources:** `docs/diagrams/class-diagram.xmi` and `docs/diagrams/sequence-diagram.xmi` remain available for tooling. Each XMI file references `docs/diagrams/xmi-to-html.xsl`, so opening the XMI directly in a modern browser will render a styled HTML view instead of a raw XML tree.

## Libraries and Tooling
- **Spring Boot Starter Web MVC:** Exposes REST endpoints with thin controllers.
- **Spring Boot Starter Data JPA:** Provides the repository abstraction and ORM support for persisting loans.
- **Spring Boot Actuator:** Adds health and metrics endpoints for runtime insights.
- **Springdoc OpenAPI Starter (WebMVC):** Generates Swagger/OpenAPI documentation for HTTP contracts.
- **MapStruct + Lombok + Lombok/MapStruct binding:** Removes DTO/entity mapping boilerplate while keeping immutable, concise models.
- **MySQL Connector/J (runtime):** Enables production persistence against MySQL; replaceable with Testcontainers/H2 in testing.
- **Devtools:** Optional hot-reload support for local development.
- **Jacoco Maven Plugin:** Generates code coverage reports during test runs.

## Integration Testing Suggestions
- **Full-stack HTTP flow:** Use `@SpringBootTest` with `@AutoConfigureMockMvc` to submit real HTTP POST requests to the calculation endpoint. Assert that the response body contains the computed monthly payment and that the repository was invoked to store the request (swap the MySQL datasource for H2 or Testcontainers to keep tests hermetic).
- **Repository integration:** Use `@DataJpaTest` to validate JPA mappings for the `Loan` entity and ensure amortization data persists and reads back correctly. Include schema assertions (e.g., column precision/scale for monetary fields) to guard against regressions.
- **Contract-first validation:** Combine OpenAPI-generated request/response schemas with MockMvc to validate that the thin controller enforces mandatory fields and passes sanitized inputs to the service.

