package com.loan_calculator.service;

import com.loan_calculator.entity.Installment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class LoanCalculationResult {
    private final List<Installment> installments;
}
