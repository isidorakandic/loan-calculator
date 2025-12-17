package com.loan_calculator;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.InstallmentDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

// Bootstraps the full application context and runs the server on a random port for end-to-end testing.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// Enables and configures MockMvc so HTTP requests can be performed without starting a real server.
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoanCalculatorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    JsonMapper jsonMapper;

    @Test
    void postLoansPersistsAndReturnsAmortizationSchedule() throws UnsupportedEncodingException {

        CreateLoanRequestDTO requestPayload = setupLoanRequest();
        List<InstallmentDTO> expectedInstallments = setUpInstallments();
        jsonMapper = JsonMapper.builder().build();
        jsonMapper.registerModule(new JavaTimeModule());

        MvcResult mvcResult = Assertions.assertDoesNotThrow(() -> mockMvc.perform(
                        MockMvcRequestBuilders.post("/loans")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonMapper.writeValueAsString(requestPayload)))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn());

        String responseBody = mvcResult.getResponse().getContentAsString();
        LoanResponseDTO actualResponse = Assertions.assertDoesNotThrow(
                () -> jsonMapper.readValue(responseBody, LoanResponseDTO.class)
        );

        Assertions.assertEquals(BigDecimal.valueOf(10000), actualResponse.getLoanAmount());
        Assertions.assertEquals(5, actualResponse.getLoanTerm());
        Assertions.assertEquals(BigDecimal.valueOf(5.3), actualResponse.getInterestRate());
        assertCreationTimestampFormat(actualResponse.getCreationTimestamp());
        List<InstallmentDTO> actualInstallments = actualResponse.getInstallments();
        assertInstallments(expectedInstallments, actualInstallments);
    }

    private CreateLoanRequestDTO setupLoanRequest() {
        CreateLoanRequestDTO createLoanRequestDTO = new CreateLoanRequestDTO();
        createLoanRequestDTO.setLoanAmount(BigDecimal.valueOf(10_000));
        createLoanRequestDTO.setInterestRate(BigDecimal.valueOf(5.3));
        createLoanRequestDTO.setLoanTerm(5);
        return createLoanRequestDTO;
    }

    private List<InstallmentDTO> setUpInstallments() {
        return List.of(
                buildInstallment(1, new BigDecimal("2026.58"), new BigDecimal("1982.41"), new BigDecimal("44.17"), new BigDecimal("8017.59")),
                buildInstallment(2, new BigDecimal("2026.58"), new BigDecimal("1991.17"), new BigDecimal("35.41"), new BigDecimal("6026.42")),
                buildInstallment(3, new BigDecimal("2026.58"), new BigDecimal("1999.96"), new BigDecimal("26.62"), new BigDecimal("4026.46")),
                buildInstallment(4, new BigDecimal("2026.58"), new BigDecimal("2008.80"), new BigDecimal("17.78"), new BigDecimal("2017.66")),
                buildInstallment(5, new BigDecimal("2026.57"), new BigDecimal("2017.66"), new BigDecimal("8.91"), new BigDecimal("0"))
        );
    }

    private InstallmentDTO buildInstallment(int month, BigDecimal paymentAmount, BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal balanceOwed) {
        InstallmentDTO installmentDTO = new InstallmentDTO();
        installmentDTO.setMonth(month);
        installmentDTO.setPaymentAmount(paymentAmount);
        installmentDTO.setPrincipalAmount(principalAmount);
        installmentDTO.setInterestAmount(interestAmount);
        installmentDTO.setBalanceOwed(balanceOwed);
        return installmentDTO;
    }

    private void assertInstallments(List<InstallmentDTO> expectedInstallments, List<InstallmentDTO> actualInstallments) {

        Assertions.assertNotNull(actualInstallments);
        Assertions.assertEquals(expectedInstallments.size(), actualInstallments.size());
        for (int i = 0; i < expectedInstallments.size(); i++) {
            InstallmentDTO expectedInst = expectedInstallments.get(i);
            InstallmentDTO actualInst = actualInstallments.get(i);
            assertThat(actualInst.getMonth()).isEqualTo(expectedInst.getMonth());
            assertThat(actualInst.getPaymentAmount()).isEqualByComparingTo(expectedInst.getPaymentAmount());
            assertThat(actualInst.getPrincipalAmount()).isEqualByComparingTo(expectedInst.getPrincipalAmount());
            assertThat(actualInst.getInterestAmount()).isEqualByComparingTo(expectedInst.getInterestAmount());
            assertThat(actualInst.getBalanceOwed()).isEqualByComparingTo(expectedInst.getBalanceOwed());
        }
    }

    private void assertCreationTimestampFormat(LocalDateTime creationTimestamp) {
        Assertions.assertNotNull(creationTimestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.ENGLISH);
        String creationTimestampString = creationTimestamp.format(formatter);
        Assertions.assertDoesNotThrow(() -> LocalDateTime.parse(creationTimestampString, formatter));
    }
}
