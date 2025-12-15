package com.loan_calculator.repository;

import com.loan_calculator.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Long> {
}
