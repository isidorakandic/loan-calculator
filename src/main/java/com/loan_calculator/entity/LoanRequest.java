package com.loan_calculator.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loan_request", uniqueConstraints = @UniqueConstraint(columnNames = {"loan_amount", "interest_rate", "loan_term"}))
@NoArgsConstructor // JPA requirement
@Getter // for MapStruct (LoanRequest -> LoanResponseDTO)
@EqualsAndHashCode // for unit testing
public class LoanRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter // for MapStruct (CreateLoanRequestDTO -> LoanRequest)
    @Column(name = "loan_amount", nullable = false)
    private BigDecimal loanAmount;

    @Setter
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @Setter
    @Column(name = "loan_term", nullable = false)
    private Integer loanTerm;

    @OneToMany(mappedBy = "loanRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Installment> installments = new ArrayList<>();

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("'CREATED'")
    private LoanStatus status;

    @Setter
    @CreationTimestamp
    private LocalDateTime creationTimestamp;

    public void setInstallments(List<Installment> installments) {
        this.installments = installments;
        installments.forEach(installment -> installment.setLoanRequest(this));
    }
}
