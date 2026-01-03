package com.loan_calculator.service;

import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoanCalculationTest {

    private final LoanCalculationImpl loanCalculation = new LoanCalculationImpl();

    @Test
    void calculateInstallments_zeroInterestRate() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("1200"));
        loanRequest.setInterestRate(BigDecimal.ZERO);
        loanRequest.setLoanTerm(3);

        List<Installment> expectedInstallments = List.of(
                new Installment(1, new BigDecimal("400.00"), new BigDecimal("400.00"), BigDecimal.ZERO, new BigDecimal("800.00")),
                new Installment(2, new BigDecimal("400.00"), new BigDecimal("400.00"), BigDecimal.ZERO, new BigDecimal("400.00")),
                new Installment(3, new BigDecimal("400.00"), new BigDecimal("400.00"), BigDecimal.ZERO, BigDecimal.ZERO)
        );

        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);
        assertInstallmentsMatchExpected(actualInstallments, expectedInstallments);
    }

    @Test
    void calculateInstallments_singleMonthLoan() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("500"));
        loanRequest.setInterestRate(new BigDecimal("12"));
        loanRequest.setLoanTerm(1);

        List<Installment> expectedInstallments = List.of(
                new Installment(1, new BigDecimal("505"), new BigDecimal("500"), new BigDecimal("5"), BigDecimal.ZERO)
        );


        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);
        assertInstallmentsMatchExpected(actualInstallments, expectedInstallments);
    }

    @Test
    void calculateInstallments_lastPaymentAdjusted() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("10000")); // principal amount
        loanRequest.setInterestRate(new BigDecimal("7"));   // annual interest rate
        loanRequest.setLoanTerm(12);

        // Expected installments precomputed manually from your data
        List<Installment> expectedInstallments = List.of(
                new Installment(1, new BigDecimal("865.27"), new BigDecimal("806.94"), new BigDecimal("58.33"), new BigDecimal("9193.06")),
                new Installment(2, new BigDecimal("865.27"), new BigDecimal("811.64"), new BigDecimal("53.63"), new BigDecimal("8381.42")),
                new Installment(3, new BigDecimal("865.27"), new BigDecimal("816.38"), new BigDecimal("48.89"), new BigDecimal("7565.04")),
                new Installment(4, new BigDecimal("865.27"), new BigDecimal("821.14"), new BigDecimal("44.13"), new BigDecimal("6743.90")),
                new Installment(5, new BigDecimal("865.27"), new BigDecimal("825.93"), new BigDecimal("39.34"), new BigDecimal("5917.97")),
                new Installment(6, new BigDecimal("865.27"), new BigDecimal("830.75"), new BigDecimal("34.52"), new BigDecimal("5087.22")),
                new Installment(7, new BigDecimal("865.27"), new BigDecimal("835.59"), new BigDecimal("29.68"), new BigDecimal("4251.63")),
                new Installment(8, new BigDecimal("865.27"), new BigDecimal("840.47"), new BigDecimal("24.80"), new BigDecimal("3411.16")),
                new Installment(9, new BigDecimal("865.27"), new BigDecimal("845.37"), new BigDecimal("19.90"), new BigDecimal("2565.79")),
                new Installment(10, new BigDecimal("865.27"), new BigDecimal("850.30"), new BigDecimal("14.97"), new BigDecimal("1715.49")),
                new Installment(11, new BigDecimal("865.27"), new BigDecimal("855.26"), new BigDecimal("10.01"), new BigDecimal("860.23")),
                new Installment(12, new BigDecimal("865.25"), new BigDecimal("860.23"), new BigDecimal("5.02"), BigDecimal.ZERO)
        );

        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);
        assertInstallmentsMatchExpected(actualInstallments, expectedInstallments);
    }

    @Test
    void calculateInstallments_longTermLoan() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("99000000"));
        loanRequest.setInterestRate(new BigDecimal("35.8"));
        loanRequest.setLoanTerm(360);

        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);

        // Ensure total number of installments is correct
        assertThat(actualInstallments).hasSize(360);

        List<Installment> expectedLastInstallments = List.of(
                new Installment(358, new BigDecimal("2953574.86"), new BigDecimal("2704238.93"), new BigDecimal("249335.93"), new BigDecimal("5653389.92")),
                new Installment(359, new BigDecimal("2953574.86"), new BigDecimal("2784915.39"), new BigDecimal("168659.47"), new BigDecimal("2868474.53")),
                new Installment(360, new BigDecimal("2954050.69"), new BigDecimal("2868474.53"), new BigDecimal("85576.16"), BigDecimal.ZERO)
        );

        // Compare only the last 3 installments
        assertInstallmentsMatchExpected(
                actualInstallments.subList(357, 360),
                expectedLastInstallments
        );
    }


    @Test
    void calculateInstallments_bigLoanAmount() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("900000000"));
        loanRequest.setInterestRate(new BigDecimal("35.8"));
        loanRequest.setLoanTerm(5);

        List<Installment> expectedInstallments = List.of(
                new Installment(1, new BigDecimal("196425605.36"), new BigDecimal("169575605.36"), new BigDecimal("26850000"), new BigDecimal("730424394.64")),
                new Installment(2, new BigDecimal("196425605.36"), new BigDecimal("174634610.92"), new BigDecimal("21790994.44"), new BigDecimal("555789783.72")),
                new Installment(3, new BigDecimal("196425605.36"), new BigDecimal("179844543.48"), new BigDecimal("16581061.88"), new BigDecimal("375945240.24")),
                new Installment(4, new BigDecimal("196425605.36"), new BigDecimal("185209905.69"), new BigDecimal("11215699.67"), new BigDecimal("190735334.55")),
                new Installment(5, new BigDecimal("196425605.36"), new BigDecimal("190735334.55"), new BigDecimal("5690270.81"), BigDecimal.ZERO)
        );

        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);
        assertInstallmentsMatchExpected(actualInstallments, expectedInstallments);
    }

    @Test
    void calculateInstallments_tinyLoanAmount() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanAmount(new BigDecimal("1"));
        loanRequest.setInterestRate(new BigDecimal("0.01"));
        loanRequest.setLoanTerm(30);

        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);

        assertThat(actualInstallments).hasSize(30);

        List<Installment> expectedFirstInstallments = List.of(
                new Installment(28, new BigDecimal("0.03"), new BigDecimal("0.03"), BigDecimal.ZERO, new BigDecimal("0.16")),
                new Installment(29, new BigDecimal("0.03"), new BigDecimal("0.03"), BigDecimal.ZERO, new BigDecimal("0.13")),
                new Installment(30, new BigDecimal("0.13"), new BigDecimal("0.13"), BigDecimal.ZERO, BigDecimal.ZERO)
        );

        assertInstallmentsMatchExpected(
                actualInstallments.subList(27, 30),
                expectedFirstInstallments
        );
    }


    private void assertInstallmentsMatchExpected(List<Installment> actual, List<Installment> expected) {
        assertThat(actual).hasSize(expected.size());

        for (int i = 0; i < expected.size(); i++) {
            Installment expectedInst = expected.get(i);
            Installment actualInst = actual.get(i);
            assertThat(actualInst.getMonth()).isEqualTo(expectedInst.getMonth());
            assertThat(actualInst.getPaymentAmount()).isEqualByComparingTo(expectedInst.getPaymentAmount());
            assertThat(actualInst.getPrincipalAmount()).isEqualByComparingTo(expectedInst.getPrincipalAmount());
            assertThat(actualInst.getInterestAmount()).isEqualByComparingTo(expectedInst.getInterestAmount());
            assertThat(actualInst.getBalanceOwed()).isEqualByComparingTo(expectedInst.getBalanceOwed());
        }
    }

}
