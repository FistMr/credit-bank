package com.puchkov.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "scoring")
@Getter
@Setter
public class ScoringProperties {

    private String baseRate;
}
