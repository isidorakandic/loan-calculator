package com.leanpay.loan_calculator;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
}
