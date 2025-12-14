package com.leanpay.loan_calculator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
public class Installment {

    @Id
    private Long id;

    @NotNull
    @Min(value = 1, message = "The month of the installment must be at least 1")
    private int month;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Payment amount must be greater than 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal paymentAmount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Principal amount must be greater than 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal principalAmount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true, message = "Interest amount must be 0 or bigger")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal interestAmount;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false, message = "Balance owed must be greater than 0")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal balanceOwed;

    @ManyToOne
    @JoinColumn(name = "loan_request_id")
    private LoanRequest loanRequest;

    public LoanRequest getLoanRequest() {
        return loanRequest;
    }

    public void setLoanRequest(LoanRequest loanRequest) {
        this.loanRequest = loanRequest;
    }
}
