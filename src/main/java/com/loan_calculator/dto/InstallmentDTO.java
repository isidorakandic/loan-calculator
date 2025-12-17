package com.loan_calculator.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor // for MapStruct (Installment -> InstallmentDTO)
@Setter  // for MapStruct
@Getter // for Jackson (InstallmentDTO -> JSON)
@EqualsAndHashCode // for integration testing
public class InstallmentDTO {

    private int month;

    private BigDecimal paymentAmount;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    private BigDecimal balanceOwed;

}
