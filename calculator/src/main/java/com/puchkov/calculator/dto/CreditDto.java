package com.puchkov.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {

    private String amount;
    private String term;
    private String monthlyPayment;
    private String rate;
    private String psk;
    private String isInsuranceEnabled;
    private String isSalaryClient;
    private String paymentSchedule;

}
