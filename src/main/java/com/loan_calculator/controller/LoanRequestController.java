package com.loan_calculator.controller;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.service.LoanRequestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor // needed for dependency injection for loanService
class LoanRequestController {

    private final LoanRequestService loanService;

    @PostMapping("/loans")
    public LoanResponseDTO createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        return loanService.createLoan(createLoanRequestDTO);
    }

    @GetMapping("/loans")
    public Page<LoanResponseDTO> getAllLoans(Pageable pageable) {
        return loanService.getAllLoans(pageable);
    }

}
