package com.loan_calculator.service;

import com.loan_calculator.entity.LoanRequest;

import java.util.concurrent.CompletableFuture;

public interface LoanService {

    CompletableFuture<LoanCalculationResult> calculate(LoanRequest loanRequest);
}
