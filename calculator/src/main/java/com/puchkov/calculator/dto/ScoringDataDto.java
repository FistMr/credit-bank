package com.puchkov.calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringDataDto {

    private String amount;
    private String term;
    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;
    private String birthdate;
    private String passportSeries;
    private String passportNumber;
    private String passportIssueDate;
    private String passportIssueBranch;
    private String maritalStatus;
    private String dependentAmount;
    private String employment;
    private String accountNumber;
    private String isInsuranceEnabled;
    private String isSalaryClient;

}
