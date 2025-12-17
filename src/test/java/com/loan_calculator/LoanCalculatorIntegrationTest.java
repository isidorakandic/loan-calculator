package com.loan_calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

// Bootstraps the full application context and runs the server on a random port for end-to-end testing.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Enables and configures MockMvc so HTTP requests can be performed without starting a real server.
@AutoConfigureMockMvc
// Activates the "test" profile to isolate configuration for integration tests.
@ActiveProfiles("test")
class LoanCalculatorIntegrationTest {

    // Injects the configured MockMvc instance to perform HTTP requests against the application context.
    @Autowired
    private MockMvc mockMvc;

    // Marks this method as a JUnit 5 test case that verifies loan requests are persisted and schedules are returned.
    @Test
    void postLoansPersistsAndReturnsAmortizationSchedule() {
        // TODO: Implement integration test for POST /loans once API contract is finalized.
        Map<String, Object> requestPayload = Map.of(
                "loanAmount", 10_000,
                "interestRate", 5.3,
                "loanTerm", 5
        );

        // Constructs the expected list of amortization installments returned by the API for the supplied input.
        List<Map<String, Object>> expectedInstallments = setUpInstallments();

        // Wraps the full expected response payload including timestamp, installments, and original loan details.
        Map<String, Object> expectedResponseBody = Map.of(
                "creationTimestamp", "17 December 2025 01:27",
                "installments", expectedInstallments,
                "interestRate", 5.3,
                "loanAmount", 10000,
                "loanTerm", 5
        );

        // TODO: Use MockMvc to POST the requestPayload and verify the response matches expectedResponseBody.
    }

    private List<Map<String, Object>> setUpInstallments() {
        List<Map<String, Object>> expectedInstallments = List.of(
                Map.of(
                        "balanceOwed", 8017.59,
                        "interestAmount", 44.17,
                        "month", 1,
                        "paymentAmount", 2026.58,
                        "principalAmount", 1982.41
                ),
                Map.of(
                        "balanceOwed", 6026.42,
                        "interestAmount", 35.41,
                        "month", 2,
                        "paymentAmount", 2026.58,
                        "principalAmount", 1991.17
                ),
                Map.of(
                        "balanceOwed", 4026.46,
                        "interestAmount", 26.62,
                        "month", 3,
                        "paymentAmount", 2026.58,
                        "principalAmount", 1999.96
                ),
                Map.of(
                        "balanceOwed", 2017.66,
                        "interestAmount", 17.78,
                        "month", 4,
                        "paymentAmount", 2026.58,
                        "principalAmount", 2008.80
                ),
                Map.of(
                        "balanceOwed", 0,
                        "interestAmount", 8.91,
                        "month", 5,
                        "paymentAmount", 2026.57,
                        "principalAmount", 2017.66
                )
        );
        return expectedInstallments;
    }
}
