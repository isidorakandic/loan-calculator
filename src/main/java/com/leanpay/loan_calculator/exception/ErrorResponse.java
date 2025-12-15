package com.leanpay.loan_calculator.exception;

import java.util.List;

public record ErrorResponse(
        List<String> errors
) {
}