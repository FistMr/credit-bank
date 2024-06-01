package com.puchkov.calculator.util;

import com.puchkov.calculator.config.Properties;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@UtilityClass
@Slf4j
public class InsuranceProcessor {

    public BigDecimal calculateInsuranceCost (LocalDate birthdate, Properties properties){
        BigDecimal insuranceCost = new BigDecimal(properties.getBaseInsuranceCost());

        int age =  Period.between(birthdate, LocalDate.now()).getYears();
        double coefficient = (double) age /100;

        insuranceCost = insuranceCost.multiply(BigDecimal.valueOf(coefficient));

        return insuranceCost;
    }

}
