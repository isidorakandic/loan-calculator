package com.leanpay.loan_calculator.controller;

import com.leanpay.loan_calculator.entity.LoanRequest;
import com.leanpay.loan_calculator.repository.LoanRequestRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
class LoanRequestController {

    @Autowired
    LoanRequestRepository loanRequestRepository;

    @PostMapping
//    public ResponseEntity<LoanResponseDTO> createLoan(
//            @Valid @RequestBody CreateLoanRequestDTO dto
//    ) {
//        LoanRequest entity = mapper.toEntity(dto);
//        LoanRequest saved = loanService.createLoan(entity);
//        return ResponseEntity.ok(mapper.toResponseDTO(saved));
//    }

    @GetMapping("/requests")
    public List<LoanRequest> getAllLoanRequests() {
        return loanRequestRepository.findAll();
    }

    @PostMapping("/requests")
    public LoanRequest createLoanRequest(@Valid @RequestBody LoanRequest loanRequest) {
        return loanRequestRepository.save(loanRequest);
    }

    @PutMapping("/requests/{id}")
    public LoanRequest updateLoanRequest(@PathVariable Long id, @Valid @RequestBody LoanRequest loanRequest) {
        loanRequest.setId(id);
        return loanRequestRepository.save(loanRequest);
    }

    @DeleteMapping("/request/{id}")
    public ResponseEntity<?> deleteLoanRequest(@PathVariable Long id) {
        LoanRequest request = loanRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Loan with ID = %d not found", id)));
        loanRequestRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
