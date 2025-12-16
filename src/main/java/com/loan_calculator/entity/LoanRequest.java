package com.loan_calculator.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor // JPA requirement
@Getter // for MapStruct (LoanRequest -> LoanResponseDTO)
@EqualsAndHashCode // for unit testing
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter // for MapStruct (CreateLoanRequestDTO -> LoanRequest)
    private BigDecimal loanAmount;

    @Setter
    private BigDecimal interestRate;

    @Setter
    private Integer loanTerm;

    @OneToMany(mappedBy = "loanRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter // for setting calculated installments in LoanService
    private List<Installment> installments = new ArrayList<>();

    @Setter
    @CreationTimestamp
    private LocalDateTime creationTimestamp;

}
