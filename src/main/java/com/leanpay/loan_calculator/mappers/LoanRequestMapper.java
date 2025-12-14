package com.leanpay.loan_calculator.mappers;

import com.leanpay.loan_calculator.dto.CreateLoanRequestDTO;
import com.leanpay.loan_calculator.dto.InstallmentDTO;
import com.leanpay.loan_calculator.dto.LoanResponseDTO;
import com.leanpay.loan_calculator.entity.Installment;
import com.leanpay.loan_calculator.entity.LoanRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanRequestMapper {


    LoanRequest toEntity(CreateLoanRequestDTO createLoanRequestDTO);

    LoanResponseDTO toResponseDTO(LoanRequest loanRequest);

    InstallmentDTO toInstallmentDTO(Installment installment);

    List<InstallmentDTO> toInstallmentDTOs(List<Installment> installments);

}
