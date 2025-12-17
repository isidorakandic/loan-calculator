package com.loan_calculator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter // for MapStruct (Installment -> InstallmentDTO)
@RequiredArgsConstructor // for installment creation in LoanService
@NoArgsConstructor // JPA requirement
@Entity
public class Installment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "installment_month")
    private Integer month;

    @NonNull
    private BigDecimal paymentAmount;

    @NonNull
    private BigDecimal principalAmount;

    @NonNull
    private BigDecimal interestAmount;

    @NonNull
    private BigDecimal balanceOwed;

    @Setter // required for setting the loan request in LoanService
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_request_id", nullable = false)
    private LoanRequest loanRequest;

}
