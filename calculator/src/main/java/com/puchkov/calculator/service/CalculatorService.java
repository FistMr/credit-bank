package com.puchkov.calculator.service;

import com.puchkov.calculator.dto.CreditDto;
import com.puchkov.calculator.dto.LoanOfferDto;
import com.puchkov.calculator.dto.LoanStatementRequestDto;
import com.puchkov.calculator.dto.ScoringDataDto;

import java.util.List;

public interface CalculatorService {

    List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto);

    CreditDto calcCreditDto(ScoringDataDto scoringDataDto);
}
