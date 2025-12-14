package com.leanpay.loan_calculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LoanResponseDTO {

    private Long id;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private int loanTerm;

    private List<Installment> installments = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public int getLoanTerm() {
        return loanTerm;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "LoanRequest { " +
                "id = " + id +
                ", loanAmount = " + loanAmount +
                ", interestRate = " + interestRate +
                ", loanTerm = " + loanTerm +
                " }";
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public void setInstallments(List<Installment> installments) {
        this.installments = installments;
    }
}
