package com.leanpay.loan_calculator;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int month;

    private BigDecimal paymentAmount;

    private BigDecimal principalAmount;

    private BigDecimal interestAmount;

    private BigDecimal balanceOwed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_request_id", nullable = false)
    private LoanRequest loanRequest;

    public LoanRequest getLoanRequest() {
        return loanRequest;
    }

    public void setLoanRequest(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
    }
}
