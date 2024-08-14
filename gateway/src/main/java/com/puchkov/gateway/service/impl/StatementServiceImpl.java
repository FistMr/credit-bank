package com.puchkov.gateway.service.impl;

import com.puchkov.gateway.dto.FinishRegistrationRequestDto;
import com.puchkov.gateway.dto.LoanOfferDto;
import com.puchkov.gateway.dto.LoanStatementRequestDto;
import com.puchkov.gateway.service.StatementService;
import com.puchkov.gateway.util.ExternalServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {

    private final ExternalServiceClient externalServiceClient;

    @Override
    public List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto) {
        ResponseEntity<List<LoanOfferDto>> responseEntity;
        responseEntity = externalServiceClient.getResponse(loanStatementRequestDto, "http://statement:8083/statement", new ParameterizedTypeReference<>() {
        });
        return responseEntity.getBody();
    }

    @Override
    public void getCreditDto(LoanOfferDto loanOfferDto) {
        externalServiceClient.sendRequest(loanOfferDto, "http://statement:8083/statement/offer");
    }

    @Override
    public void saveCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID uuid) {
        externalServiceClient.sendRequest(finishRegistrationRequestDto, "http://deal:8082/deal/calculate/" + uuid);
    }
}
