package com.puchkov.calculator.dto;

import com.puchkov.calculator.dto.enums.EmploymentStatus;
import com.puchkov.calculator.dto.enums.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentDto {

    @NotNull
    private EmploymentStatus employmentStatus;

    @Pattern(regexp = "^\\d{10}$|^\\d{12}$")
    @NotNull
    private String employerINN;

    @NotNull
    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal salary;

    @NotNull
    private Position position;

    @NotNull
    private Integer workExperienceTotal;

    @NotNull
    private Integer workExperienceCurrent;

}
