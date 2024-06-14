package com.puchkov.deal.dto;

import com.puchkov.deal.enums.Gender;
import com.puchkov.deal.enums.MaritalStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinishRegistrationRequestDto {

    @NotNull
    private Gender gender;

    @NotNull
    private MaritalStatus maritalStatus;

    @NotNull
    private Integer dependentAmount;

    @NotNull
    @Past
    @Schema(example = "2019-11-06")
    private LocalDate passportIssueDate;

    @NotBlank
    @Schema(example = "УМВД России по г. Севастополю")
    private String passportIssueBranch;

    @NotNull
    @Valid
    private EmploymentDto employment;

    @NotBlank
    private String accountNumber;
}