package com.leanpay.loan_calculator;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Entity
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @DecimalMin(value = "1.0", message = "Loan amount must be at least 1")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin(value = "0.0", message = "Interest rate must be >= 0")
    @DecimalMax(value = "100.0", message = "Interest rate must be <= 100")
    @Digits(integer = 3, fraction = 2)
    private BigDecimal interestRate;

    @NotNull
    @Min(value = 1, message = "Loan term must be at least 1 month")
    private int loanTerm;

    @Override
    public String toString() {
        return "LoanRequest { " +
                "id = " + id +
                ", loanAmount = " + loanAmount +
                ", interestRate = " + interestRate +
                ", loanTerm = " + loanTerm +
                " }";
    }
}
