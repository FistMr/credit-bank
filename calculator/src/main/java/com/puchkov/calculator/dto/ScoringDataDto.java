package com.puchkov.calculator.dto;

import com.puchkov.calculator.dto.enums.Gender;
import com.puchkov.calculator.dto.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDto {

    @NotNull
    @DecimalMin(value = "30000.0", message = "Сумма кредита - действительно число, большее или равное 30000.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @Min(value = 6, message = "Срок кредита - целое число, большее или равное 6.")
    private Integer term;

    @NotBlank
    @Size(min = 2,max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Имя - от 2 до 30 латинских букв.")
    @Schema(example = "Pavel")
    private String firstName;

    @Size(min = 2,max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия - от 2 до 30 латинских букв.")
    @Schema(example = "Ilyich")
    private String lastName;

    @NotBlank
    @Size(min = 2,max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Отчество - от 2 до 30 латинских букв.")
    @Schema(example = "Puchkov")
    private String middleName;

    @NotNull
    private Gender gender;

    @Past
    @NotNull
    @Schema(example = "2001-11-06")
    private LocalDate birthdate;

    @Pattern(regexp = "[0-9]{4}", message = "Серия паспорта - 4 цифры")
    private String passportSeries;

    @Pattern(regexp = "[0-9]{6}", message = "Номер паспорта - 6 цифр.")
    private String passportNumber;

    @Past
    @NotNull
    @Schema(example = "2019-07-11")
    private LocalDate passportIssueDate;

    @NotBlank
    @Size(min = 2,max = 100)
    @Pattern(regexp = "^[\\w\\W]+$", message = "Место выдачи паспорта - от 2 до 100 латинских букв.")
    @Schema(example = "УМВД России по г. Севастополю")
    private String passportIssueBranch;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    private Integer dependentAmount;

    @NotNull
    @Valid
    private EmploymentDto employment;

    @NotBlank
    private String accountNumber;

    @NotNull
    private Boolean isInsuranceEnabled;

    @NotNull
    private Boolean isSalaryClient;

}
