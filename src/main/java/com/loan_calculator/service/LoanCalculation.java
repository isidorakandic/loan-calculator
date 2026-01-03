package com.loan_calculator.service;

import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public interface LoanCalculation {

    CompletableFuture<List<Installment>> async_calculateInstallments(LoanRequest loanRequest);
}
