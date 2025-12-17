package com.loan_calculator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
    }
}
