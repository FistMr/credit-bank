package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.entity.Passport;
import org.springframework.stereotype.Component;

@Component
public class PassportMapper {

    public Passport DtoToEntity(LoanStatementRequestDto loanStatementRequestDto){
        return Passport.builder()
                .series(loanStatementRequestDto.getPassportSeries())
                .number(loanStatementRequestDto.getPassportNumber())
                .build();
    }



}
