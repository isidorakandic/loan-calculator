package com.leanpay.loan_calculator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor // for MapStruct (LoanRequest -> LoanResponseDTO)
@Setter
@Getter // for Jackson (LoanResponseDTO -> JSON)
public class LoanResponseDTO {

    private Long id;

    private float loanAmount;

    private float interestRate;

    private int loanTerm;

    private List<InstallmentDTO> installments = new ArrayList<>();

}
