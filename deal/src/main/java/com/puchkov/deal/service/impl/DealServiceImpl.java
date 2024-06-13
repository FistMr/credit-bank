package com.puchkov.deal.service.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.service.DealService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealServiceImpl implements DealService {
    @Override
    public void createClientAndStatement(LoanStatementRequestDto loanStatementRequestDto) {

    }

    @Override
    public List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto) {
        return null;
    }
}
