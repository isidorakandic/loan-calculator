package com.loan_calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.LoanRequest;
import com.loan_calculator.repository.LoanRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanRequestIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanRequestRepository loanRequestRepository;

    @BeforeEach
    void cleanDatabase() {
        loanRequestRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /loans persists loan request and returns full amortization schedule")
    void createLoan_persistsEntityAndReturnsSchedule() throws Exception {
        CreateLoanRequestDTO requestDTO = new CreateLoanRequestDTO();
        requestDTO.setLoanAmount(new BigDecimal("15000"));
        requestDTO.setInterestRate(new BigDecimal("4.5"));
        requestDTO.setLoanTerm(24);

        MvcResult result = mockMvc.perform(post("/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanAmount").value(15000))
                .andExpect(jsonPath("$.interestRate").value(4.5))
                .andExpect(jsonPath("$.loanTerm").value(24))
                .andExpect(jsonPath("$.installments").isArray())
                .andExpect(jsonPath("$.installments.length()").value(24))
                .andExpect(jsonPath("$.creationTimestamp").exists())
                .andReturn();

        LoanResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), LoanResponseDTO.class);

        assertThat(response.getInstallments()).isNotEmpty();
        assertThat(response.getInstallments().getFirst().getPaymentAmount()).isGreaterThan(BigDecimal.ZERO);
        assertThat(response.getInstallments().getLast().getBalanceOwed().setScale(2)).isEqualByComparingTo("0.00");

        List<LoanRequest> savedRequests = loanRequestRepository.findAll();
        assertThat(savedRequests).hasSize(1);
        LoanRequest saved = savedRequests.getFirst();
        assertThat(saved.getLoanAmount()).isEqualByComparingTo("15000");
        assertThat(saved.getInterestRate()).isEqualByComparingTo("4.5");
        assertThat(saved.getLoanTerm()).isEqualTo(24);
        assertThat(saved.getInstallments()).hasSize(24);
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

        assertThat(loanRequestRepository.count()).isZero();
    }
}
