package com.loan_calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.dto.InstallmentDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset H2 between tests without direct repository access
class LoanRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /loans persists loan request and returns full amortization schedule")
    void createLoan_persistsEntityAndReturnsSchedule() throws Exception {
        // Arrange: build a request that should reproduce the provided amortization schedule
        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO();
        requestDTO.setLoanAmount(new BigDecimal("10000"));
        requestDTO.setInterestRate(new BigDecimal("5.3"));
        requestDTO.setLoanTerm(5);

        // Expected amortization schedule used to assert both persistence and calculation accuracy
        List<InstallmentDTO> expectedSchedule = new ArrayList<>();
        expectedSchedule.add(buildInstallment(1, "2026.58", "1982.41", "44.17", "8017.59"));
        expectedSchedule.add(buildInstallment(2, "2026.58", "1991.17", "35.41", "6026.42"));
        expectedSchedule.add(buildInstallment(3, "2026.58", "1999.96", "26.62", "4026.46"));
        expectedSchedule.add(buildInstallment(4, "2026.58", "2008.80", "17.78", "2017.66"));
        expectedSchedule.add(buildInstallment(5, "2026.57", "2017.66", "8.91", "0.00"));

        // Act: create the loan through the API
        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanAmount").value(10000))
                .andExpect(jsonPath("$.interestRate").value(5.3))
                .andExpect(jsonPath("$.loanTerm").value(5))
                .andExpect(jsonPath("$.installments.length()").value(5))
                .andExpect(jsonPath("$.creationTimestamp").exists());

        // Assert: fetch all loans through the read endpoint to confirm persistence and integrity
        MvcResult fetchResult = mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andReturn();

        List<LoanResponseDTO> savedLoans = objectMapper.readValue(
                fetchResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, LoanResponseDTO.class));

        assertThat(savedLoans).hasSize(1);
        LoanResponseDTO savedLoan = savedLoans.getFirst();
        assertThat(savedLoan.getLoanAmount()).isEqualByComparingTo("10000");
        assertThat(savedLoan.getInterestRate()).isEqualByComparingTo("5.3");
        assertThat(savedLoan.getLoanTerm()).isEqualTo(5);
        assertThat(savedLoan.getInstallments()).hasSize(5);
        assertScheduleMatches(expectedSchedule, savedLoan.getInstallments());
    }

    @Test
    @DisplayName("POST /loans returns validation errors for invalid payload")
    void createLoan_returnsValidationErrors() throws Exception {
        CreateLoanRequestDTO invalidRequest = new CreateLoanRequestDTO();
        invalidRequest.setLoanAmount(new BigDecimal("0"));
        invalidRequest.setInterestRate(new BigDecimal("-1"));
        invalidRequest.setLoanTerm(0);

        mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").value(org.hamcrest.Matchers.hasItems(
                        "Loan amount must be at least 1.",
                        "Interest rate must be >= 0.",
                        "Loan term must be at least 1 month."
                )));

        // Verify the GET endpoint returns an empty list, proving nothing was persisted
        MvcResult fetchResult = mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andReturn();

        List<LoanResponseDTO> savedLoans = objectMapper.readValue(
                fetchResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructCollectionType(List.class, LoanResponseDTO.class));
        assertThat(savedLoans).isEmpty();
    }

    private InstallmentDTO buildInstallment(int month, String payment, String principal, String interest, String balanceOwed) {
        InstallmentDTO dto = new InstallmentDTO();
        dto.setMonth(month);
        dto.setPaymentAmount(new BigDecimal(payment));
        dto.setPrincipalAmount(new BigDecimal(principal));
        dto.setInterestAmount(new BigDecimal(interest));
        dto.setBalanceOwed(new BigDecimal(balanceOwed));
        return dto;
    }

    private void assertScheduleMatches(List<InstallmentDTO> expected, List<InstallmentDTO> actual) {
        assertThat(actual).hasSameSizeAs(expected);
        for (int i = 0; i < expected.size(); i++) {
            InstallmentDTO expectedInstallment = expected.get(i);
            InstallmentDTO actualInstallment = actual.get(i);
            assertThat(actualInstallment.getMonth()).isEqualTo(expectedInstallment.getMonth());
            assertThat(actualInstallment.getPaymentAmount()).isEqualByComparingTo(expectedInstallment.getPaymentAmount());
            assertThat(actualInstallment.getPrincipalAmount()).isEqualByComparingTo(expectedInstallment.getPrincipalAmount());
            assertThat(actualInstallment.getInterestAmount()).isEqualByComparingTo(expectedInstallment.getInterestAmount());
            assertThat(actualInstallment.getBalanceOwed()).isEqualByComparingTo(expectedInstallment.getBalanceOwed());
        }
    }
}
