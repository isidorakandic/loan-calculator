package com.loan_calculator.mappers;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.InstallmentDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {

    @Mapping(target = "installments", ignore = true)
    @Mapping(target = "creationTimestamp", ignore = true)
    LoanRequest toEntity(CreateLoanRequestDTO createLoanRequestDTO);

    LoanResponseDTO toResponseDTO(LoanRequest loanRequest);

    InstallmentDTO toInstallmentDTO(Installment installment);

    @IterableMapping(elementTargetType = InstallmentDTO.class)
    List<InstallmentDTO> toInstallmentDTOs(List<Installment> installments);

    @IterableMapping(elementTargetType = LoanResponseDTO.class)
    List<LoanResponseDTO> toLoanResponseDTOs(List<LoanRequest> loanRequests);

}
