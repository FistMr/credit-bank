package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client DtoToEntity(LoanStatementRequestDto loanStatementRequestDto){
        return Client.builder()
                .lastName(loanStatementRequestDto.getLastName())
                .firstName(loanStatementRequestDto.getFirstName())
                .middleName(loanStatementRequestDto.getMiddleName())
                .birthDate(loanStatementRequestDto.getBirthdate())
                .email(loanStatementRequestDto.getEmail())
                .build();
    }

}
