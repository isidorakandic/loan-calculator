package com.leanpay.loan_calculator.repository;

import com.leanpay.loan_calculator.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
}
