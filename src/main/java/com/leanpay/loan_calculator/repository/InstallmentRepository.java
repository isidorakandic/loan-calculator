package com.leanpay.loan_calculator.repository;

import com.leanpay.loan_calculator.entity.Installment;
import com.leanpay.loan_calculator.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstallmentRepository extends JpaRepository<Installment, Long> {

    List<Installment> findByLoanRequest(LoanRequest loanRequest);
}
