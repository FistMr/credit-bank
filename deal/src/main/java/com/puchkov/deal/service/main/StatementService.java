package com.puchkov.deal.service.main;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDtoDto;
import java.util.List;

public interface StatementService {

    List<LoanOfferDto> createClientAndStatement(LoanStatementRequestDtoDto loanStatementRequestDto);
}
