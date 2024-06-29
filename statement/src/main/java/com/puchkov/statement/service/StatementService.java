package com.puchkov.statement.service;

import com.puchkov.statement.dto.LoanOfferDto;
import com.puchkov.statement.dto.LoanStatementRequestDto;

import java.util.List;

public interface StatementService {

    List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto);

    void getCreditDto(LoanOfferDto loanOfferDto);
}
