package com.leanpay.loan_calculator;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findByLoanRequest(LoanRequest loanRequest);
}
