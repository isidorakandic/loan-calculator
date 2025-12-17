package com.loan_calculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.InstallmentDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

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

    // Configures a Jackson ObjectMapper locally to serialize and deserialize payloads without relying on context beans.
    private final ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
            // Registers JavaTimeModule so LocalDateTime fields are serialized consistently with the API configuration.
            .modules(new JavaTimeModule())
            // Disables timestamp writing to keep date-time formatting aligned with human-readable patterns.
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            // Builds the fully configured ObjectMapper instance for test serialization and deserialization.
            .build();

    // Marks this method as a JUnit 5 test case that verifies loan requests are persisted and schedules are returned.
    @Test
    void postLoansPersistsAndReturnsAmortizationSchedule() {
        // Creates a DTO representing the request payload for POST /loan to favor compile-time safety over Maps.
        CreateLoanRequestDTO requestPayload = new CreateLoanRequestDTO();
        // Sets the loan amount to 10,000 to simulate a medium-sized loan request.
        requestPayload.setLoanAmount(BigDecimal.valueOf(10_000));
        // Sets the interest rate to 5.3% to align with the expected amortization schedule.
        requestPayload.setInterestRate(BigDecimal.valueOf(5.3));
        // Sets the loan term to 5 months to keep the example deterministic and easy to verify.
        requestPayload.setLoanTerm(5);

        // Constructs the expected list of amortization installments returned by the API for the supplied input.
        List<InstallmentDTO> expectedInstallments = setUpInstallments();

        // Performs the POST request to /loan with the request DTO serialized as JSON and asserts an OK status.
        MvcResult mvcResult = Assertions.assertDoesNotThrow(() -> mockMvc.perform( // Executes the request and fails the test if serialization or MVC throws.
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/loan") // Configures a POST to the /loan endpoint.
                                .contentType(MediaType.APPLICATION_JSON) // Sends JSON so the controller binds to CreateLoanRequestDTO.
                                .content(objectMapper.writeValueAsString(requestPayload))) // Serializes the request DTO into the HTTP body.
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk()) // Asserts the endpoint responds with HTTP 200 OK.
                .andReturn()); // Captures the full MVC result for response body inspection.

        // Extracts the raw JSON response body for further inspection.
        String responseBody = mvcResult.getResponse().getContentAsString();
        // Deserializes the response into a strongly typed DTO to improve field-level validation and readability.
        LoanResponseDTO actualResponse = Assertions.assertDoesNotThrow(
                () -> objectMapper.readValue(responseBody, LoanResponseDTO.class)
        );

        // Verifies that the creation timestamp exists and matches the configured format without assuming exact value.
        assertCreationTimestampFormat(actualResponse.getCreationTimestamp());

        // Confirms that the echoed loan amount matches the request within a small tolerance for formatting.
        assertNumericEquals(BigDecimal.valueOf(10_000), actualResponse.getLoanAmount(), "loanAmount");
        // Confirms that the echoed loan term matches the request exactly.
        Assertions.assertEquals(5, actualResponse.getLoanTerm());
        // Confirms that the echoed interest rate matches the request within a small tolerance for formatting.
        assertNumericEquals(BigDecimal.valueOf(5.3), actualResponse.getInterestRate(), "interestRate");

        // Retrieves the installments from the response for comparison against the expected schedule.
        List<InstallmentDTO> actualInstallments = actualResponse.getInstallments();
        // Asserts that each installment matches the precomputed expectations within a rounding tolerance.
        assertInstallments(expectedInstallments, actualInstallments);
    }

    // Builds the deterministic list of expected installments for the 5-month, 5.3% interest, 10,000 loan.
    private List<InstallmentDTO> setUpInstallments() {
        // Creates an immutable list of InstallmentDTO objects mirroring the amortization schedule returned by the API.
        List<InstallmentDTO> expectedInstallments = List.of(
                // First installment reflects the initial payment breakdown and remaining balance.
                buildInstallment(1, 2026.58, 1982.41, 44.17, 8017.59),
                // Second installment continues the declining interest and increasing principal composition.
                buildInstallment(2, 2026.58, 1991.17, 35.41, 6026.42),
                // Third installment tracks the midpoint of the schedule.
                buildInstallment(3, 2026.58, 1999.96, 26.62, 4026.46),
                // Fourth installment nears payoff with lower interest and higher principal.
                buildInstallment(4, 2026.58, 2008.80, 17.78, 2017.66),
                // Final installment zeroes out the balance with a slightly reduced payment due to rounding.
                buildInstallment(5, 2026.57, 2017.66, 8.91, 0)
        );
        // Returns the fully populated expected schedule to the caller.
        return expectedInstallments;
    }

    // Helper factory that creates an InstallmentDTO with all monetary values supplied as doubles for readability.
    private InstallmentDTO buildInstallment(int month, double paymentAmount, double principalAmount,
                                            double interestAmount, double balanceOwed) {
        // Instantiates a new InstallmentDTO to hold amortization details.
        InstallmentDTO installmentDTO = new InstallmentDTO();
        // Records which month in the schedule this installment represents.
        installmentDTO.setMonth(month);
        // Stores the total payment amount for the month using BigDecimal for precision.
        installmentDTO.setPaymentAmount(BigDecimal.valueOf(paymentAmount));
        // Stores the principal component of the payment.
        installmentDTO.setPrincipalAmount(BigDecimal.valueOf(principalAmount));
        // Stores the interest component of the payment.
        installmentDTO.setInterestAmount(BigDecimal.valueOf(interestAmount));
        // Stores the remaining balance after the payment.
        installmentDTO.setBalanceOwed(BigDecimal.valueOf(balanceOwed));
        // Returns the fully populated installment object.
        return installmentDTO;
    }

    // Asserts that every expected installment matches its counterpart from the API response.
    private void assertInstallments(List<InstallmentDTO> expectedInstallments,
                                    List<InstallmentDTO> actualInstallments) {
        // Ensures the response contained an installment list.
        Assertions.assertNotNull(actualInstallments, "Installments should be returned in the response");
        // Confirms that the API returned the same number of installments as expected.
        Assertions.assertEquals(expectedInstallments.size(), actualInstallments.size(),
                "Installments count should match");

        // Iterates through each installment to compare field-by-field values.
        for (int i = 0; i < expectedInstallments.size(); i++) {
            // Grabs the expected installment at the current index.
            InstallmentDTO expected = expectedInstallments.get(i);
            // Grabs the actual installment at the same index from the response.
            InstallmentDTO actual = actualInstallments.get(i);

            // Asserts that the payment amount matches within tolerance to account for serialization rounding.
            assertNumericEquals(expected.getPaymentAmount(), actual.getPaymentAmount(),
                    "paymentAmount at index " + i);
            // Asserts that the principal portion matches within tolerance.
            assertNumericEquals(expected.getPrincipalAmount(), actual.getPrincipalAmount(),
                    "principalAmount at index " + i);
            // Asserts that the interest portion matches within tolerance.
            assertNumericEquals(expected.getInterestAmount(), actual.getInterestAmount(),
                    "interestAmount at index " + i);
            // Asserts that the remaining balance matches within tolerance.
            assertNumericEquals(expected.getBalanceOwed(), actual.getBalanceOwed(),
                    "balanceOwed at index " + i);
            // Verifies that the month index is preserved exactly.
            Assertions.assertEquals(expected.getMonth(), actual.getMonth(), "month at index " + i);
        }
    }

    // Compares two numeric values represented as BigDecimal while allowing minor rounding differences.
    private void assertNumericEquals(BigDecimal expectedValue, BigDecimal actualValue, String message) {
        // Ensures the actual value is present before comparison.
        Assertions.assertNotNull(actualValue, message + " should be present");
        // Compares the two values using a tolerance to avoid scale-related equality issues.
        Assertions.assertEquals(expectedValue.doubleValue(), actualValue.doubleValue(), 0.01,
                message + " should match");
    }

    // Validates that the creation timestamp is present and matches the configured display format.
    private void assertCreationTimestampFormat(LocalDateTime creationTimestamp) {
        // Guards against a missing timestamp in the response.
        Assertions.assertNotNull(creationTimestamp, "creationTimestamp should be present");

        // Defines the formatter used by the API so the test checks the same format.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH);
        // Converts the timestamp into its string representation using the known formatter.
        String creationTimestampString = creationTimestamp.format(formatter);
        // Parses the string back to a LocalDateTime to verify round-trip correctness and catch formatting issues.
        Assertions.assertDoesNotThrow(() -> LocalDateTime.parse(creationTimestampString, formatter),
                "creationTimestamp should match format 'dd MMMM yyyy HH:mm'");
    }
}
