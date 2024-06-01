package com.puchkov.calculator.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
@Slf4j
public class MonthlyPaymentProcessor {

    public BigDecimal calculate(BigDecimal totalAmount, Integer term, BigDecimal rate) {
        log.debug("MonthlyPaymentProcessor: calculate(Entrance) parameters: totalAmount = {}" +
                ",term = {}, rate = {}",totalAmount,term,rate );
        BigDecimal monthlyRate = rate.divide(new BigDecimal(1200), 10, RoundingMode.UP);

        //  (  (1+mounthlyRate)^term  )  -1
        BigDecimal firstStep = BigDecimal.ONE.add(monthlyRate).pow(term);
        BigDecimal secondStep = firstStep.subtract(BigDecimal.ONE);

        // mounthlyRate + ( mounthlyRate / secondStep )
        BigDecimal thirdStep = monthlyRate.add(monthlyRate.divide(secondStep, 10, RoundingMode.UP));

        BigDecimal monthlyPayment = totalAmount.multiply(thirdStep);

        log.debug("MonthlyPaymentProcessor: calculate(Entrance) response: totalAmount = {}", monthlyPayment);

        return monthlyPayment;
    }
}
