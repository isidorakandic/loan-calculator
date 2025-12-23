package com.loan_calculator.service;

import com.loan_calculator.entity.LoanRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Primary
public class LoanServiceProxy implements LoanService {

    private final LoanService loanServiceImpl;

    LoanServiceProxy(@Qualifier("loanServiceImpl") LoanService loanServiceImpl) {
        this.loanServiceImpl = loanServiceImpl;
    }

    @Override
    public CompletableFuture<LoanCalculationResult> calculate(LoanRequest loanRequest) {
        return loanServiceImpl.calculate(loanRequest);
    }
}
