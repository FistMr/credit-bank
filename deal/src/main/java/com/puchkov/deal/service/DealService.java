package com.puchkov.deal.service;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import java.util.List;

public interface DealService {

    void createClientAndStatement(LoanStatementRequestDto loanStatementRequestDto);

    List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto);
}
