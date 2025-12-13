package com.leanpay.loan_calculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
class LoanCalculatorController {


    @GetMapping("/hello")
    public String sayHi() {
        return "Hello Spring Boot world!";
    }
}
