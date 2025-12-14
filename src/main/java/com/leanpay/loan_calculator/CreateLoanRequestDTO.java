package com.leanpay.loan_calculator;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "A request to calculate loan installments")
public class CreateLoanRequestDTO {

    @NotNull
    @DecimalMin(value = "1.0", message = "Loan amount must be at least 1")
    @Digits(integer = 12, fraction = 2)
    @Schema(description = "Total loan amount in EUR", example = "5000.0")
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin(value = "0.0", message = "Interest rate must be >= 0")
    @DecimalMax(value = "100.0", message = "Interest rate must be <= 100")
    @Digits(integer = 3, fraction = 2)
    @Schema(description = "Annual interest rate in %", example = "5.25")
    private BigDecimal interestRate;

    @NotNull
    @Min(value = 1, message = "Loan term must be at least 1 month")
    @Schema(description = "Loan term in months", example = "12")
    private int loanTerm;

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public void setLoanTerm(int loanTerm) {
        this.loanTerm = loanTerm;
    }

//    @Override
//    public String toString() {
//        return "LoanRequest { " +
//                "id = " + id +
//                ", loanAmount = " + loanAmount +
//                ", interestRate = " + interestRate +
//                ", loanTerm = " + loanTerm +
//                " }";
//    }
}
