package com.loan_calculator.service;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import com.loan_calculator.entity.LoanStatus;
import com.loan_calculator.mappers.LoanRequestMapper;
import com.loan_calculator.repository.LoanRequestRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor // for dependency injection for loanRequestRepository and loanRequestMapper
public class LoanRequestService {

    private final LoanRequestRepository loanRequestRepository;
    private final LoanRequestMapper loanRequestMapper;
    private final LoanService loanService;

    public Page<LoanResponseDTO> getAllLoans(Pageable pageable) {
        return loanRequestRepository.findAll(pageable).map(loanRequestMapper::toResponseDTO);
    }

    public LoanResponseDTO createLoan(CreateLoanRequestDTO createLoanRequestDTO) {

        Optional<LoanRequest> existingLoanRequest = findExistingLoanRequest(createLoanRequestDTO);

        if (existingLoanRequest.isPresent()) {
            return loanRequestMapper.toResponseDTO(existingLoanRequest.get());
        }

        LoanRequest loanRequest = loanRequestMapper.toEntity(createLoanRequestDTO);
        loanRequest.setStatus(LoanStatus.CREATED);

        CompletableFuture<LoanCalculationResult> calculationResultFuture = loanService.calculate(loanRequest);
        LoanCalculationResult calculationResult = calculationResultFuture.join();
        List<Installment> installments = calculationResult.getInstallments();
        loanRequest.setInstallments(installments);

        try {
            loanRequest = loanRequestRepository.saveAndFlush(loanRequest);
        } catch (DataIntegrityViolationException exception) {
            LoanRequest persistedLoan = findExistingLoanRequest(createLoanRequestDTO)
                    .orElseThrow(() -> exception);
            return loanRequestMapper.toResponseDTO(persistedLoan);
        }

        return loanRequestMapper.toResponseDTO(loanRequest);
    }

    private Optional<LoanRequest> findExistingLoanRequest(CreateLoanRequestDTO createLoanRequestDTO) {
        return loanRequestRepository.findByLoanAmountAndInterestRateAndLoanTerm(
                createLoanRequestDTO.getLoanAmount(),
                createLoanRequestDTO.getInterestRate(),
                createLoanRequestDTO.getLoanTerm());
    }
}
