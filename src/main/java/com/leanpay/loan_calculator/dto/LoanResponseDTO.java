package com.leanpay.loan_calculator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor // for MapStruct (LoanRequest -> LoanResponseDTO)
@Setter
@Getter // for Jackson (LoanResponseDTO -> JSON)
public class LoanResponseDTO {

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private BigDecimal loanTerm;

    private List<InstallmentDTO> installments = new ArrayList<>();

}
