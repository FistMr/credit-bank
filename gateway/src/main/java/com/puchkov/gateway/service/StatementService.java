package com.puchkov.gateway.service;

import com.puchkov.gateway.dto.FinishRegistrationRequestDto;
import com.puchkov.gateway.dto.LoanOfferDto;
import com.puchkov.gateway.dto.LoanStatementRequestDto;

import java.util.List;
import java.util.UUID;

public interface StatementService {


    List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto);

    void getCreditDto(LoanOfferDto loanOfferDto);

    void saveCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID uuid);
}
