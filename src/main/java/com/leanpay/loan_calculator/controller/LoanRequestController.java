package com.leanpay.loan_calculator.controller;

import com.leanpay.loan_calculator.dto.CreateLoanRequestDTO;
import com.leanpay.loan_calculator.dto.LoanResponseDTO;
import com.leanpay.loan_calculator.entity.LoanRequest;
import com.leanpay.loan_calculator.mappers.LoanRequestMapper;
import com.leanpay.loan_calculator.service.LoanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor // needed for dependency injection for loanService and loanRequestMapper
class LoanRequestController {

    private final LoanService loanService;
    private final LoanRequestMapper loanRequestMapper;


    @PostMapping("/loans")
    public ResponseEntity<LoanResponseDTO> createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        LoanRequest newRequest = loanRequestMapper.toEntity(createLoanRequestDTO);
        LoanRequest loanWithInstallments = loanService.createLoan(newRequest); // error handling?
        return ResponseEntity.ok(loanRequestMapper.toResponseDTO(loanWithInstallments));
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanResponseDTO>> getAllLoans() {
        List<LoanRequest> loanRequests = loanService.getAllLoans();
        return ResponseEntity.ok(loanRequestMapper.toLoanResponseDTOs(loanRequests));
    }

}
