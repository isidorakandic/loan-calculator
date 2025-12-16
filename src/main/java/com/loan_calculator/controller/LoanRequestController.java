package com.loan_calculator.controller;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.service.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor // needed for dependency injection for loanService and loanRequestMapper
class LoanRequestController {

    private final LoanService loanService;

    @PostMapping("/loans")
    public LoanResponseDTO createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        return loanService.createLoan(createLoanRequestDTO);
    }

}
