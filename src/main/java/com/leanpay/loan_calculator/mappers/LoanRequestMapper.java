package com.leanpay.loan_calculator.mappers;

import com.leanpay.loan_calculator.dto.CreateLoanRequestDTO;
import com.leanpay.loan_calculator.dto.InstallmentDTO;
import com.leanpay.loan_calculator.dto.LoanResponseDTO;
import com.leanpay.loan_calculator.entity.Installment;
import com.leanpay.loan_calculator.entity.LoanRequest;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {

    int SCALE = 2;


    LoanRequest toEntity(CreateLoanRequestDTO createLoanRequestDTO);

    LoanResponseDTO toResponseDTO(LoanRequest loanRequest);

    InstallmentDTO toInstallmentDTO(Installment installment);

    @IterableMapping(elementTargetType = InstallmentDTO.class)
    List<InstallmentDTO> toInstallmentDTOs(List<Installment> installments);

    // MapStruct automatically recognizes and applies this rounding method to all BigDecimal values
    default float roundToTwoDecimals(BigDecimal value) {
        if (value == null) return 0;
        return value.setScale(2, RoundingMode.HALF_UP).floatValue();
    }
}
