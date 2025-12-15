package com.leanpay.loan_calculator.service;

import com.leanpay.loan_calculator.entity.Installment;
import com.leanpay.loan_calculator.entity.LoanRequest;
import com.leanpay.loan_calculator.repository.LoanRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor // need for dependency injection for loanRequestRepository
public class LoanService {

    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN; // banker's rounding
    private static final MathContext MC = new MathContext(20, ROUNDING);
    private final LoanRequestRepository loanRequestRepository;

    @Transactional
    public LoanRequest createLoan(LoanRequest createLoanRequest) {

        List<Installment> installments = calculateInstallments(createLoanRequest);

        installments.forEach(installment -> installment.setLoanRequest(createLoanRequest));
        createLoanRequest.setInstallments(installments);
        return loanRequestRepository.save(createLoanRequest);
    }

    private List<Installment> calculateInstallments(LoanRequest loanRequest) {
        BigDecimal principal = loanRequest.getLoanAmount();
        BigDecimal interestRate = loanRequest.getInterestRate();
        int term = loanRequest.getLoanTerm();

        BigDecimal monthlyInterest = calculateMonthlyInterest(interestRate);
        BigDecimal monthlyPayment = calculateMonthlyPayment(principal, monthlyInterest, term);

        ArrayList<Installment> installments = new ArrayList<>();
        BigDecimal remainingBalance = principal;
        for (int month = 1; month <= term; month++) {
            BigDecimal interestPaid = remainingBalance.multiply(monthlyInterest, MC);
            BigDecimal principalPaid = monthlyPayment.subtract(interestPaid, MC);
            remainingBalance = remainingBalance.subtract(principalPaid, MC);
            Installment installment = new Installment(month, monthlyPayment, principalPaid, interestPaid, remainingBalance);
            installments.add(installment);
        }
        return installments;
    }

    // Formula used for calculating monthly payment amount: P x i(1 + i)**n / (1 + i)**n - 1
    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyInterest, int term) {
        if (monthlyInterest.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(term), MC);
        }
        BigDecimal one = BigDecimal.ONE;
        BigDecimal powered = one.add(monthlyInterest, MC).pow(term, MC);
        BigDecimal dividend = powered.multiply(monthlyInterest, MC).multiply(principal, MC);
        BigDecimal devisor = powered.subtract(one, MC);
        return dividend.divide(devisor, MC);
    }

    private BigDecimal calculateMonthlyInterest(BigDecimal interestRate) {
        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal twelve = BigDecimal.valueOf(12);
        return interestRate.divide(hundred, MC).divide(twelve, MC);
    }
}
