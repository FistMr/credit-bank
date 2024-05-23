package com.puchkov.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDto {

    private String amount;
    private String term;
    private String firstName;
    private String lastName;
    private String middleName;
    private Enum gender;
    private String birthdate;
    private String passportSeries;
    private String passportNumber;
    private String passportIssueDate;
    private String passportIssueBranch;
    private Enum maritalStatus;
    private String dependentAmount;
    private EmploymentDto employment;
    private String accountNumber;
    private String isInsuranceEnabled;
    private String isSalaryClient;

}
