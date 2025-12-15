package com.loan_calculator.mappers;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.InstallmentDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import org.mapstruct.AfterMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {

    LoanRequest toEntity(CreateLoanRequestDTO createLoanRequestDTO);

    LoanResponseDTO toResponseDTO(LoanRequest loanRequest);

    InstallmentDTO toInstallmentDTO(Installment installment);

    @IterableMapping(elementTargetType = InstallmentDTO.class)
    List<InstallmentDTO> toInstallmentDTOs(List<Installment> installments);

    @IterableMapping(elementTargetType = LoanResponseDTO.class)
    List<LoanResponseDTO> toLoanResponseDTOs(List<LoanRequest> loanRequests);

    @AfterMapping
    default void roundDto(@MappingTarget InstallmentDTO dto) {
        dto.setPrincipalAmount(roundToTwoDecimals(dto.getPrincipalAmount()));
        dto.setInterestAmount(roundToTwoDecimals(dto.getInterestAmount()));
        dto.setBalanceOwed(roundToTwoDecimals(dto.getBalanceOwed()));
        dto.setPaymentAmount(roundToTwoDecimals(dto.getPaymentAmount()));
    }

    private BigDecimal roundToTwoDecimals(BigDecimal value) {
        if (value == null) return null;
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
