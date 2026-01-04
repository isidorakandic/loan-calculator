package com.loan_calculator.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

    @ParameterizedTest(name = "{0}") // {0} is a placeholder for the 1. argument that's passed to the method
    @MethodSource("testCasesArgumentsProvider")
    void calculateInstallments(LoanRequest loanRequest, List<Installment> expectedInstallments) {
        List<Installment> actualInstallments = loanCalculation.calculateInstallments(loanRequest);
        List<Installment> actualInstallmentToBeChecked =
                actualInstallments.subList(actualInstallments.size() - expectedInstallments.size(), actualInstallments.size());
        assertInstallmentsMatchExpected(actualInstallmentToBeChecked, expectedInstallments);
    }

    private static Stream<Arguments> testCasesArgumentsProvider() throws IOException {
        InputStream inputStream = LoanCalculationTest.class.getResourceAsStream("/loan-cases.json");
        JsonMapper mapper = JsonMapper.builder()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();

        // we map to ArrayNode instead of mapping directly ro LoanRequest because we want to read 'name' property as well
        ArrayNode nodes = (ArrayNode) mapper.readTree(inputStream);

        return StreamSupport.stream(nodes.spliterator(), false)
                .map(node -> {
                    LoanRequest loanRequest = mapper.convertValue(node, LoanRequest.class);
                    List<Installment> expectedInstallments = new ArrayList<>(loanRequest.getInstallments());
                    loanRequest.setInstallments(new ArrayList<>());
                    return Arguments.of(Named.of(node.get("name").asText(), loanRequest), expectedInstallments);
                });
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
