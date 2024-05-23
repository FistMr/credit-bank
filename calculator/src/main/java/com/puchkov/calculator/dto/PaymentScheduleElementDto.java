package com.puchkov.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentScheduleElementDto {

    private String number;
    private String date;
    private String totalPayment;
    private String interestPayment;
    private String debtPayment;
    private String remainingDebt;

}
