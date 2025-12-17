package com.loan_calculator.controller;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.service.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor // needed for dependency injection for loanService and loanRequestMapper
class LoanRequestController {

    private final LoanService loanService;

    // Handles loan creation requests and returns the calculated amortization schedule
    @PostMapping("/loans")
    public LoanResponseDTO createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        return loanService.createLoan(createLoanRequestDTO);
    }

    // Exposes a simple read endpoint so tests (and clients) can fetch all persisted loans
    @GetMapping("/loans")
    public List<LoanResponseDTO> getAllLoans() {
        return loanService.getAllLoans();
    }

}
