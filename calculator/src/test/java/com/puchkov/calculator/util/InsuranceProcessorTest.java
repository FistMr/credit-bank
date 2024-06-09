package com.puchkov.calculator.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceProcessorTest {

    @Test
    void calculateInsuranceCost() {
        LocalDate birthdate = LocalDate.of(2001, 6, 11);
        BigDecimal insuranceCost = new BigDecimal("100000");
        int expectedAge = Period.between(birthdate, LocalDate.now()).getYears();

        BigDecimal calculatedCost = InsuranceProcessor.calculateInsuranceCost(birthdate, insuranceCost);

        BigDecimal expectedCoefficient = BigDecimal.valueOf((double) expectedAge / 100);
        BigDecimal expectedCalculatedCost = insuranceCost.multiply(expectedCoefficient);

        assertEquals(calculatedCost,expectedCalculatedCost);
    }
}