package com.loan_calculator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor // for Jackson (JSON -> Java Object)
@Setter // for Jackson
@Getter // for MapStruct (CreateLoanRequestDTO -> LoanRequest)
@Schema(description = "A request to calculate loan installments")
public class CreateLoanRequestDTO {

    @NotNull
    @DecimalMin(value = "1.0", message = "Loan amount must be at least 1.")
    @Digits(integer = 12, fraction = 2)
    @Schema(description = "Total amount of the loan", example = "18000")
    private BigDecimal loanAmount;

    @NotNull
    @DecimalMin(value = "0.0", message = "Interest rate must be >= 0.")
    @DecimalMax(value = "100.0", message = "Interest rate must be <= 100.")
    @Schema(description = "Annual interest rate in %", example = "5.3")
    private BigDecimal interestRate;

    @NotNull
    @Min(value = 1, message = "Loan term must be at least 1 month.")
    @Schema(description = "Loan term in months", example = "60")
    private int loanTerm;

}
