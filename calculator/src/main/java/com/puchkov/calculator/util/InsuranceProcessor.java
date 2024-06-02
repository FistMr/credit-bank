package com.puchkov.calculator.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@UtilityClass
@Slf4j
public class InsuranceProcessor {

    public BigDecimal calculateInsuranceCost(LocalDate birthdate, BigDecimal insuranceCost) {
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        double coefficient = (double) age / 100;

        insuranceCost = insuranceCost.multiply(BigDecimal.valueOf(coefficient));

        return insuranceCost;
    }

}
