package com.puchkov.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "myapp")
@Getter
@Setter
public class Properties {

    private String baseRate;
    private String baseInsuranceCost;
    private String minCreditAge;
    private String maxCreditAge;
    private String minWorkExperienceTotal;
    private String minWorkExperienceCurrent;
    private String minFemaleRateAge;
    private String maxFemaleRateAge;
    private String minMaleRateAge;
    private String maxMaleRateAge;
}
