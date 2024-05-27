package com.puchkov.calculator.dto;

import com.puchkov.calculator.dto.enums.EmploymentStatus;
import com.puchkov.calculator.dto.enums.Position;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Pattern(regexp = "^\\d{10}$|^\\d{12}$", message = "ИНН - 10 или 12 цифр")
    @NotNull
    private String employerINN;

    @NotNull
    @DecimalMin(value = "0.0")
    @Digits(integer = 10, fraction = 2)
    @Schema(example = "60000")
    private BigDecimal salary;

    @NotNull
    private Position position;

    @NotNull
    @Schema(example = "25")
    private Integer workExperienceTotal;

    @NotNull
    @Schema(example = "12")
    private Integer workExperienceCurrent;

}
