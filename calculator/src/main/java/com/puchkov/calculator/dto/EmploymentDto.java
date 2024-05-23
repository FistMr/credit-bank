package com.puchkov.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {

    private Enum employmentStatus;
    private String employerINN;
    private BigDecimal salary;
    private Enum position;
    private Integer workExperienceTotal;
    private Integer workExperienceCurrent;

}
