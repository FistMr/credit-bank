package com.puchkov.statement.service.impl;

import com.puchkov.statement.dto.LoanOfferDto;
import com.puchkov.statement.dto.LoanStatementRequestDto;
import com.puchkov.statement.service.StatementService;
import com.puchkov.statement.util.ExternalServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private final ExternalServiceClient externalServiceClient;

    @Override
    public List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto) {
        ResponseEntity<List<LoanOfferDto>> responseEntity;
        responseEntity = externalServiceClient.getResponse(loanStatementRequestDto, "/statement", new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public void getCreditDto(LoanOfferDto loanOfferDto) {
        externalServiceClient.sendRequest(loanOfferDto, "/offer/select");
    }
}
