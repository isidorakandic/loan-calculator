package com.loan_calculator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.loan_calculator.entity.LoanStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor // for MapStruct (LoanRequest -> LoanResponseDTO)
@Setter
@Getter // for Jackson (LoanResponseDTO -> JSON)
@EqualsAndHashCode // for unit testing
public class LoanResponseDTO {

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private int loanTerm;

    private LoanStatus status;

    private List<InstallmentDTO> installments = new ArrayList<>();

    @JsonFormat(pattern = "dd MMMM yyyy HH:mm")
    private LocalDateTime creationTimestamp;

}
