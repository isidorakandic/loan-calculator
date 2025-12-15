package com.leanpay.loan_calculator.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor // for MapStruct (Installment -> InstallmentDTO)
@Setter  // for MapStruct
@Getter // for Jackson (InstallmentDTO -> JSON)
public class InstallmentDTO {

    private Long id;

    private int month;

    private float paymentAmount;

    private float principalAmount;

    private float interestAmount;

    private float balanceOwed;

}
