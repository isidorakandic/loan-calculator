package com.loan_calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LoanCalculatorApplication {

    static void main(String[] args) {
        SpringApplication.run(LoanCalculatorApplication.class, args);
    }

}
