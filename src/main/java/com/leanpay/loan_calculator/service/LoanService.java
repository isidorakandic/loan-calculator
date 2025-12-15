package com.leanpay.loan_calculator.service;

import com.leanpay.loan_calculator.entity.Installment;
import com.leanpay.loan_calculator.entity.LoanRequest;
import com.leanpay.loan_calculator.repository.LoanRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN; // banker's rounding
    private static final MathContext MC = new MathContext(20, ROUNDING);

    @Autowired
    LoanRequestRepository loanRequestRepository;

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
        for (int i = 0; i < term; i++) {
            // check logic for the last month, and for 0 interest.
            BigDecimal interestPaid = remainingBalance.multiply(monthlyInterest, MC); // interest paid this month
            BigDecimal principalPaid = monthlyPayment.subtract(interestPaid, MC);
            remainingBalance = remainingBalance.subtract(principalPaid, MC);
            Installment installment = new Installment(i + 1, monthlyPayment, principalPaid, interestPaid, remainingBalance);
            installments.add(installment);
        }
        return installments;
    }

    private BigDecimal calculateMonthlyInterest(BigDecimal interestRate) {
        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal twelve = BigDecimal.valueOf(12);
        return interestRate.divide(hundred, MC).divide(twelve, MC);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal principal, BigDecimal monthlyInterest, int term) {
        BigDecimal one = BigDecimal.ONE;
        // P x i(1 + i)n / (1 + i)n - 1
        BigDecimal powered = one.add(monthlyInterest, MC).pow(term, MC); // (1 + i)n
        BigDecimal dividend = powered.multiply(monthlyInterest, MC).multiply(principal, MC);
        BigDecimal devisor = powered.subtract(one, MC);
        return dividend.divide(devisor, MC);
    }
}
