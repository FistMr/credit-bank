package com.puchkov.statement.dto;

import com.puchkov.statement.validation.Adult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatementRequestDto implements RequestAbleDto {

    @NotNull
    @DecimalMin(value = "30000.0", message = "Сумма кредита - действительное число, большее или равное 30000.")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal amount;

    @NotNull
    @Min(value = 6, message = "Срок кредита - целое число, большее или равное 6.")
    private Integer term;

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Имя - от 2 до 30 латинских букв.")
    @Schema(example = "Pavel")
    private String firstName;

    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Фамилия - от 2 до 30 латинских букв.")
    @Schema(example = "Ilyich")
    private String lastName;

    @NotBlank
    @Size(min = 2, max = 30)
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Отчество - от 2 до 30 латинских букв.")
    @Schema(example = "Puchkov")
    private String middleName;

    @Email
    @Schema(example = "pavel.pu4ckow@yandex.ru")
    private String email;

    @Past
    @NotNull
    @Adult(message = "возраст меньше 18 лет")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd")
    @Schema(example = "2001-11-06")
    private LocalDate birthdate;

    @Pattern(regexp = "[0-9]{4}", message = "Серия паспорта - 4 цифры")
    private String passportSeries;

    @Pattern(regexp = "[0-9]{6}", message = "Номер паспорта - 6 цифр.")
    private String passportNumber;

}
