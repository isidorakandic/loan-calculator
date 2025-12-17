# Class overview for Lucidchart AI prompt

Use the following descriptions to seed Lucidchart AI with the main classes, their key properties, behaviors, and relationships in the loan calculator application.

## LoanCalculatorApplication (Spring Boot entrypoint)
- **Purpose**: Bootstraps the Spring Boot context.
- **Key actions**: Runs the application via `SpringApplication.run`.
- **Relationships**: No direct collaborators; loads the controller, service, repository, mapper, and entity beans.

## LoanRequestController (REST layer)
- **Purpose**: Exposes the `/loans` POST endpoint to calculate and persist a loan schedule.
- **Key properties**: Injects `LoanService`.
- **Key actions**: `createLoan(CreateLoanRequestDTO)` validates input JSON and delegates to the service; returns a `LoanResponseDTO`.
- **Relationships**: Calls `LoanService`. Accepts `CreateLoanRequestDTO`; returns `LoanResponseDTO`.

## LoanService (business logic)
- **Purpose**: Performs amortization math, builds domain entities, and persists results.
- **Key properties**: Injects `LoanRequestRepository` and `LoanRequestMapper`; uses math constants for rounding and precision.
- **Key actions**:
  - `createLoan(CreateLoanRequestDTO)`: maps DTO to `LoanRequest`, calculates installments, associates them, saves via repository, and maps back to response DTO.
  - `calculateInstallments(LoanRequest)`: derives monthly payment, principal/interest split per month, and remaining balance list.
- **Relationships**: Uses `LoanRequestMapper` for DTO <-> entity conversions; persists through `LoanRequestRepository`; aggregates `Installment` entities onto a `LoanRequest`.

## LoanRequestRepository (data access)
- **Purpose**: Persists `LoanRequest` aggregates with Spring Data JPA.
- **Key actions**: Inherits CRUD operations from `JpaRepository<LoanRequest, Long>`.
- **Relationships**: Operates on `LoanRequest` entities (which own `Installment` children).

## LoanRequest (domain entity)
- **Purpose**: Represents a single loan request aggregate with calculated installments.
- **Key properties**: `loanAmount`, `interestRate`, `loanTerm`, `creationTimestamp`, `installments` (One-to-Many cascade), generated `id`.
- **Relationships**: Owns multiple `Installment` entities (cascade + orphan removal). Used by `LoanService` for calculations and by `LoanRequestRepository` for persistence.

## Installment (domain entity)
- **Purpose**: Represents one monthly payment line in an amortization schedule.
- **Key properties**: `month`, `paymentAmount`, `principalAmount`, `interestAmount`, `balanceOwed`, generated `id`, `loanRequest` reference.
- **Relationships**: Many-to-One back to `LoanRequest`; created and attached by `LoanService`.

## LoanRequestMapper (MapStruct mapper)
- **Purpose**: Converts between DTOs and entities.
- **Key actions**:
  - `toEntity(CreateLoanRequestDTO)` (ignores installments and creation timestamp on input).
  - `toResponseDTO(LoanRequest)` and `toInstallmentDTO(Installment)` for outbound responses.
  - Iterable mapping helpers for lists of loans/installments.
- **Relationships**: Used by `LoanService` to map request DTO to entity and entity back to response DTO graph.

## DTOs (request/response shapes)
- **CreateLoanRequestDTO**: Validated input with `loanAmount`, `interestRate`, `loanTerm`.
- **LoanResponseDTO**: Outbound view with loan terms, `creationTimestamp`, and `installments` list.
- **InstallmentDTO**: Outbound installment view with amounts and remaining balance.
- **Relationships**: Flow through controller (input/output), mapped to/from entities via `LoanRequestMapper`.

## GlobalExceptionHandler and ErrorResponse
- **Purpose**: Centralizes API error handling and logging.
- **Key actions**: Translates validation errors, JSON parse errors, and unexpected exceptions into `ErrorResponse` payloads (list of error messages).
- **Relationships**: Intercepts controller exceptions; `ErrorResponse` is the response body shape.
