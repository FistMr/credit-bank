package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.ScoringDataDto;
import com.puchkov.deal.entity.Client;
import com.puchkov.deal.entity.Statement;
import org.springframework.stereotype.Component;

@Component
public class ScoringDataDtoMapper {

    public ScoringDataDto createDto(Statement statement, Client client, FinishRegistrationRequestDto finishRegistrationRequestDto){
        return ScoringDataDto.builder()
                .amount(statement.getAppliedOffer().getRequestedAmount())
                .term(statement.getAppliedOffer().getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .gender(finishRegistrationRequestDto.getGender())
                .birthdate(client.getBirthDate())
                .passportSeries(client.getPassport().getSeries())
                .passportNumber(client.getPassport().getNumber())
                .passportIssueDate(finishRegistrationRequestDto.getPassportIssueDate())
                .passportIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch())
                .maritalStatus(finishRegistrationRequestDto.getMaritalStatus())
                .dependentAmount(finishRegistrationRequestDto.getDependentAmount())
                .employment(finishRegistrationRequestDto.getEmployment())
                .accountNumber(finishRegistrationRequestDto.getAccountNumber())
                .isInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnabled())
                .isSalaryClient(statement.getAppliedOffer().getIsSalaryClient())
                .build();
    }

}
