package com.loan_calculator.service;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import com.loan_calculator.mappers.LoanRequestMapper;
import com.loan_calculator.repository.LoanRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor // for dependency injection for loanRequestRepository
public class LoanService {

    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN; // banker's rounding
    private static final MathContext MC = new MathContext(20, ROUNDING);
    private static final int MONEY_SCALE = 2;
    private final LoanRequestRepository loanRequestRepository;
    private final LoanRequestMapper loanRequestMapper;

    public Page<LoanResponseDTO> getAllLoans(Pageable pageable) {
        return loanRequestRepository.findAll(pageable)
                .map(loanRequestMapper::toResponseDTO);
    }

    public LoanResponseDTO createLoan(CreateLoanRequestDTO createLoanRequestDTO) {

        LoanRequest createLoanRequest = loanRequestMapper.toEntity(createLoanRequestDTO);

        List<Installment> installments = calculateInstallments(createLoanRequest);

        LoanRequest finalCreateLoanRequest = createLoanRequest;
        installments.forEach(installment -> installment.setLoanRequest(finalCreateLoanRequest));
        createLoanRequest.setInstallments(installments);

        createLoanRequest = loanRequestRepository.save(createLoanRequest);

        return loanRequestMapper.toResponseDTO(createLoanRequest);
    }

    protected List<Installment> calculateInstallments(LoanRequest loanRequest) {
        BigDecimal principal = loanRequest.getLoanAmount();
        BigDecimal interestRate = loanRequest.getInterestRate();
        int term = loanRequest.getLoanTerm();

        BigDecimal monthlyInterest = calculateMonthlyInterest(interestRate);
        BigDecimal theoreticalMonthlyPayment = calculateMonthlyPayment(principal, monthlyInterest, term);
        BigDecimal fixedMonthlyPayment = theoreticalMonthlyPayment.setScale(MONEY_SCALE, ROUNDING);

        List<Installment> installments = new ArrayList<>();
        BigDecimal remainingBalance = principal;

        for (int month = 1; month <= term; month++) {
            BigDecimal theoreticalInterest = remainingBalance.multiply(monthlyInterest, MC);
            BigDecimal roundedInterest = theoreticalInterest.setScale(MONEY_SCALE, ROUNDING);
            BigDecimal roundedPrincipal;
            if (month < term) {
                BigDecimal theoreticalPrincipal = fixedMonthlyPayment.subtract(theoreticalInterest, MC);
                roundedPrincipal = theoreticalPrincipal.setScale(MONEY_SCALE, ROUNDING);
                remainingBalance = remainingBalance.subtract(roundedPrincipal, MC);
            } else {
                roundedPrincipal = remainingBalance.setScale(MONEY_SCALE, ROUNDING);
                remainingBalance = BigDecimal.ZERO;
            }
            BigDecimal payment = roundedPrincipal.add(roundedInterest);
            installments.add(new Installment(month, payment, roundedPrincipal, roundedInterest, remainingBalance));
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
