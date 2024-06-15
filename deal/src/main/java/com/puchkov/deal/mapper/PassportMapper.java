package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.LoanStatementRequestDtoDto;
import com.puchkov.deal.entity.Passport;
import org.springframework.stereotype.Component;

@Component
public class PassportMapper {

    public Passport dtoToEntity(LoanStatementRequestDtoDto loanStatementRequestDto){
        return Passport.builder()
                .series(loanStatementRequestDto.getPassportSeries())
                .number(loanStatementRequestDto.getPassportNumber())
                .build();
    }

public void updateEntity(Passport passport, FinishRegistrationRequestDto finishRegistrationRequestDto){
    passport.setIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch());
    passport.setIssueDate(finishRegistrationRequestDto.getPassportIssueDate());
}

}
