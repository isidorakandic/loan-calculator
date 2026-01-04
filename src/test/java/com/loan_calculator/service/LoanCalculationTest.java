package com.loan_calculator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.loan_calculator.entity.Installment;
import com.loan_calculator.entity.LoanRequest;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

class LoanCalculationTest {

    private final LoanCalculationImpl loanCalculation = new LoanCalculationImpl();

    @ParameterizedTest(name = "{0}")
    @MethodSource("loanRequestProvider")
    void calculateInstallments(LoanRequest loanRequest, List<Installment> expectedInstallments) {
        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);
        List<Installment> actualInstallmentToBeChecked =
                actualInstallments.subList(actualInstallments.size() - expectedInstallments.size(), actualInstallments.size());
        assertInstallmentsMatchExpected(actualInstallmentToBeChecked, expectedInstallments);
    }

    private static Stream<Arguments> loanRequestProvider() throws IOException {
        InputStream inputStream = LoanCalculationTest.class.getResourceAsStream("/loan-cases.json");
        if (inputStream == null) {
            throw new IllegalStateException("loan-cases.json not found");
        }
        try (inputStream) {
            JsonMapper jsonMapper = JsonMapper.builder().build();
            JsonNode rootNode = jsonMapper.readTree(inputStream);

            if (rootNode == null || !rootNode.isArray()) {
                throw new IllegalStateException("loan-cases.json is not an array");
            }

            return StreamSupport.stream(rootNode.spliterator(), false).map(node -> {
                ObjectNode loanRequestNode = node.deepCopy();
                String name = loanRequestNode.path("name").asText();
                if (name == null || name.isBlank()) {
                    throw new IllegalStateException("Test case name is missing in loan-cases.json");
                }
                loanRequestNode.remove("name");

                LoanRequest loanRequest = jsonMapper.convertValue(loanRequestNode, LoanRequest.class);

                List<Installment> expectedInstallments = new ArrayList<>(loanRequest.getInstallments());
                loanRequest.setInstallments(new ArrayList<>());
                return Arguments.of(Named.of(name, loanRequest), expectedInstallments);
            });
        }
    }


    private static void assertInstallmentsMatchExpected(List<Installment> actual, List<Installment> expected) {
        assertThat(actual).hasSize(expected.size());

        for (int i = expected.size() - 1; i >= 0; i--) {
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
