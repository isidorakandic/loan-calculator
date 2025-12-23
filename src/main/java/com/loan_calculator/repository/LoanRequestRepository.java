package com.loan_calculator.repository;

import com.loan_calculator.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {

    Optional<LoanRequest> findByLoanAmountAndInterestRateAndLoanTerm(BigDecimal loanAmount, BigDecimal interestRate, Integer loanTerm);
}
