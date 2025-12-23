package com.loan_calculator.mappers;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.InstallmentDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import com.loan_calculator.entity.LoanStatus;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoanRequestMapperTest {

    private final LoanRequestMapper mapper = Mappers.getMapper(LoanRequestMapper.class);
    private final LocalDateTime timestamp = LocalDateTime.now();

    @Test
    void loanDTOtoEntity() {
        CreateLoanRequestDTO input = generateLoanRequestDTO();
        LoanRequest expected = generateCreateLoanRequest();

        LoanRequest result = mapper.toEntity(input);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void loanEntityToDTO() {
        LoanRequest input = generateLoanRequest();
        LoanResponseDTO expected = generateLoanResponseDTO();
        LoanResponseDTO result = mapper.toResponseDTO(input);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void installmentEntityToDTO() {
        Installment input = generateInstallment();
        InstallmentDTO expected = generateInstallmentDTO();
        InstallmentDTO result = mapper.toInstallmentDTO(input);
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void listOfInstallmentsToDTOs() {
        List<Installment> input = generateInstallmentList();
        List<InstallmentDTO> expected = generateInstallmentDTOlist();
        List<InstallmentDTO> result = mapper.toInstallmentDTOs(input);
        validateInstallmentDtoList(expected, result);
    }

    @Test
    void shouldMapLoanRequestListToResponseDTOList() {
        List<LoanRequest> input = generateLoanRequestList();
        List<LoanResponseDTO> expected = generateLoanResponseList();
        List<LoanResponseDTO> result = mapper.toLoanResponseDTOs(input);
        validateLoanResponseDtoList(expected, result);

    }

    private CreateLoanRequestDTO generateLoanRequestDTO() {
        CreateLoanRequestDTO loanRequestDTO = new CreateLoanRequestDTO();
        loanRequestDTO.setLoanAmount(new BigDecimal("4500"));
        loanRequestDTO.setInterestRate(new BigDecimal("2.5"));
        loanRequestDTO.setLoanTerm(3);
        return loanRequestDTO;

    }

    private LoanResponseDTO generateLoanResponseDTO() {
        LoanResponseDTO loanResponseDTO = new LoanResponseDTO();
        loanResponseDTO.setLoanAmount(new BigDecimal("4500"));
        loanResponseDTO.setInterestRate(new BigDecimal("2.5"));
        loanResponseDTO.setLoanTerm(3);
        loanResponseDTO.setStatus(LoanStatus.CREATED);
        loanResponseDTO.setCreationTimestamp(timestamp);
        loanResponseDTO.setInstallments(generateInstallmentDTOlist());
        return loanResponseDTO;
    }

    private InstallmentDTO generateInstallmentDTO() {
        InstallmentDTO installment = new InstallmentDTO();
        installment.setMonth(1);
        installment.setPaymentAmount(new BigDecimal("1506.25"));
        installment.setPrincipalAmount(new BigDecimal("1496.88"));
        installment.setInterestAmount(new BigDecimal("9.37"));
        installment.setBalanceOwed(new BigDecimal("3003.12"));
        return installment;
    }

    private LoanRequest generateLoanRequest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("4500"));
        loanRequest.setInterestRate(new BigDecimal("2.5"));
        loanRequest.setLoanTerm(3);
        loanRequest.setStatus(LoanStatus.CREATED);
        loanRequest.setCreationTimestamp(timestamp);
        loanRequest.setInstallments(generateInstallmentList());
        return loanRequest;
    }

    private LoanRequest generateCreateLoanRequest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("4500"));
        loanRequest.setInterestRate(new BigDecimal("2.5"));
        loanRequest.setLoanTerm(3);
        return loanRequest;
    }

    private Installment generateInstallment() {
        return new Installment(1, new BigDecimal("1506.25"), new BigDecimal("1496.88"), new BigDecimal("9.37"), new BigDecimal("3003.12"));

    }

    private List<Installment> generateInstallmentList() {
        return List.of(
                new Installment(1, new BigDecimal("1506.25"), new BigDecimal("1496.88"), new BigDecimal("9.37"), new BigDecimal("3003.12")),
                new Installment(2, new BigDecimal("1506.25"), new BigDecimal("1499.99"), new BigDecimal("6.26"), new BigDecimal("1503.13"))
        );
    }

    private List<InstallmentDTO> generateInstallmentDTOlist() {
        InstallmentDTO installment1 = new InstallmentDTO();
        installment1.setMonth(1);
        installment1.setPaymentAmount(new BigDecimal("1506.25"));
        installment1.setPrincipalAmount(new BigDecimal("1496.88"));
        installment1.setInterestAmount(new BigDecimal("9.37"));
        installment1.setBalanceOwed(new BigDecimal("3003.12"));

        InstallmentDTO installment2 = new InstallmentDTO();
        installment2.setMonth(2);
        installment2.setPaymentAmount(new BigDecimal("1506.25"));
        installment2.setPrincipalAmount(new BigDecimal("1499.99"));
        installment2.setInterestAmount(new BigDecimal("6.26"));
        installment2.setBalanceOwed(new BigDecimal("1503.13"));
        return List.of(installment1, installment2);
    }

    private List<LoanRequest> generateLoanRequestList() {
        LoanRequest first = new LoanRequest();
        first.setLoanAmount(new BigDecimal("5000"));
        first.setInterestRate(new BigDecimal("4.5"));
        first.setLoanTerm(12);
        first.setStatus(LoanStatus.CREATED);
        first.setCreationTimestamp(timestamp);
        first.setInstallments(generateInstallmentList());

        LoanRequest second = new LoanRequest();
        second.setLoanAmount(new BigDecimal("8000"));
        second.setInterestRate(new BigDecimal("6.0"));
        second.setLoanTerm(24);
        second.setStatus(LoanStatus.CREATED);
        second.setCreationTimestamp(timestamp);
        second.setInstallments(generateInstallmentList());

        return List.of(first, second);

    }

    private List<LoanResponseDTO> generateLoanResponseList() {
        LoanResponseDTO first = new LoanResponseDTO();
        first.setLoanAmount(new BigDecimal("5000"));
        first.setInterestRate(new BigDecimal("4.5"));
        first.setLoanTerm(12);
        first.setStatus(LoanStatus.CREATED);
        first.setCreationTimestamp(timestamp);
        first.setInstallments(generateInstallmentDTOlist());

        LoanResponseDTO second = new LoanResponseDTO();
        second.setLoanAmount(new BigDecimal("8000"));
        second.setInterestRate(new BigDecimal("6.0"));
        second.setLoanTerm(24);
        second.setStatus(LoanStatus.CREATED);
        second.setCreationTimestamp(timestamp);
        second.setInstallments(generateInstallmentDTOlist());

        return List.of(first, second);

    }

    private void validateInstallmentDtoList(List<InstallmentDTO> expected, List<InstallmentDTO> actual) {
        assertThat(expected.size()).isEqualTo(actual.size());
        for (int i = 0; i < expected.size(); i++) {
            validateInstallmentDTO(expected.get(i), actual.get(i));
        }
    }

    private void validateInstallmentDTO(InstallmentDTO expected, InstallmentDTO actual) {
        assertThat(expected.getMonth()).isEqualTo(actual.getMonth());
        assertThat(expected.getPaymentAmount()).isEqualTo(actual.getPaymentAmount());
        assertThat(expected.getPrincipalAmount()).isEqualTo(actual.getPrincipalAmount());
        assertThat(expected.getInterestAmount()).isEqualTo(actual.getInterestAmount());
        assertThat(expected.getBalanceOwed()).isEqualTo(actual.getBalanceOwed());
    }

    private void validateLoanResponseDtoList(List<LoanResponseDTO> expected, List<LoanResponseDTO> actual) {
        assertThat(expected.size()).isEqualTo(actual.size());
        for (int i = 0; i < expected.size(); i++) {
            validateLoanResponseDTO(expected.get(i), actual.get(i));
        }
    }

    private void validateLoanResponseDTO(LoanResponseDTO expected, LoanResponseDTO actual) {
        assertThat(expected.getLoanTerm()).isEqualTo(actual.getLoanTerm());
        assertThat(expected.getLoanAmount()).isEqualTo(actual.getLoanAmount());
        assertThat(expected.getInterestRate()).isEqualTo(actual.getInterestRate());
        assertThat(expected.getCreationTimestamp()).isEqualTo(actual.getCreationTimestamp());
        assertThat(expected.getStatus()).isEqualTo(actual.getStatus());
        validateInstallmentDtoList(expected.getInstallments(), actual.getInstallments());
    }
}
