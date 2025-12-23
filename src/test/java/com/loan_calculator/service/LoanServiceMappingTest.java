package com.loan_calculator.service;

import com.loan_calculator.dto.CreateLoanRequestDTO;
import com.loan_calculator.dto.LoanResponseDTO;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import com.loan_calculator.entity.LoanStatus;
import com.loan_calculator.mappers.LoanRequestMapper;
import com.loan_calculator.repository.LoanRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceMappingTest {

    @Mock
    private LoanRequestRepository loanRequestRepository;

    @Mock
    private LoanRequestMapper loanRequestMapper;

    @Spy
    @InjectMocks
    private LoanService loanService;

    CreateLoanRequestDTO requestDTO;
    LoanRequest loanRequest;
    LoanResponseDTO responseDTO;
    List<Installment> installments;

    @BeforeEach
    void setUp() {
        requestDTO = new CreateLoanRequestDTO();
        loanRequest = new LoanRequest();
        responseDTO = new LoanResponseDTO();

        Installment installment1 = new Installment();
        Installment installment2 = new Installment();
        installments = List.of(installment1, installment2);

        when(loanRequestMapper.toEntity(requestDTO)).thenReturn(loanRequest);
        doReturn(installments).when(loanService).calculateInstallments(loanRequest);
        when(loanRequestRepository.findByLoanAmountAndInterestRateAndLoanTerm(any(), any(), any()))
                .thenReturn(Optional.empty());

        when(loanRequestRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRequestMapper.toResponseDTO(loanRequest)).thenReturn(responseDTO);
    }

    @Test
    void createLoan_shouldCallAllCollaboratorsAndReturnResponse() {
        LoanResponseDTO result = loanService.createLoan(requestDTO);

        assertThat(result).isSameAs(responseDTO);
        verify(loanRequestMapper).toEntity(requestDTO);
        verify(loanService).calculateInstallments(loanRequest);
        verify(loanRequestRepository).saveAndFlush(loanRequest);
        verify(loanRequestMapper).toResponseDTO(loanRequest);

    }

    @Test
    void createLoan_shouldAttachLoanRequestToInstallments_beforeSaving() {
        loanService.createLoan(requestDTO);

        ArgumentCaptor<LoanRequest> captor = ArgumentCaptor.forClass(LoanRequest.class);
        verify(loanRequestRepository).saveAndFlush(captor.capture());
        LoanRequest savedLoanRequest = captor.getValue();

        assertThat(savedLoanRequest.getInstallments())
                .allSatisfy(i -> assertSame(savedLoanRequest, i.getLoanRequest()));
    }

    @Test
    void createLoan_shouldSetStatusToCreatedBeforeSaving() {
        loanService.createLoan(requestDTO);

        ArgumentCaptor<LoanRequest> captor = ArgumentCaptor.forClass(LoanRequest.class);
        verify(loanRequestRepository).saveAndFlush(captor.capture());

        assertThat(captor.getValue().getStatus()).isEqualTo(LoanStatus.CREATED);
    }

    @Test
    void createLoan_whenExistingLoanFound_shouldReturnExisting() {
        LoanRequest existingLoanRequest = new LoanRequest();
        when(loanRequestRepository.findByLoanAmountAndInterestRateAndLoanTerm(any(), any(), any()))
                .thenReturn(Optional.of(existingLoanRequest));
        when(loanRequestMapper.toResponseDTO(existingLoanRequest)).thenReturn(responseDTO);

        LoanResponseDTO result = loanService.createLoan(requestDTO);

        assertThat(result).isSameAs(responseDTO);
        verify(loanRequestRepository, never()).saveAndFlush(any());
        verify(loanRequestRepository, never()).save(any());
        verify(loanRequestMapper, never()).toEntity(any());
        verify(loanService, never()).calculateInstallments(any());
    }

}
