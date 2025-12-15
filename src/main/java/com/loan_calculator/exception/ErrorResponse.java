package com.loan_calculator.exception;

import java.util.List;

public record ErrorResponse(
        List<String> errors
) {
}