package com.puchkov.deal.service.main.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDtoDto;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.entity.Client;
import com.puchkov.deal.entity.Passport;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.ExternalServiceException;
import com.puchkov.deal.mapper.ClientMapper;
import com.puchkov.deal.mapper.PassportMapper;
import com.puchkov.deal.mapper.StatementMapper;
import com.puchkov.deal.repository.ClientRepository;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.auxiliary.ExternalServiceClient;
import com.puchkov.deal.service.auxiliary.StatusHistoryManager;
import com.puchkov.deal.service.main.StatementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatementServiceImpl implements StatementService {

    private final ClientRepository clientRepository;

    private final StatementRepository statementRepository;

    private final PassportMapper passportMapper;

    private final ClientMapper clientMapper;

    private final StatusHistoryManager statusHistoryManager;

    private final StatementMapper statementMapper;

    private final ExternalServiceClient externalServiceClient;

    @Override
    @Transactional
    public List<LoanOfferDto> createClientAndStatement(LoanStatementRequestDtoDto requestDto) {

        Passport passport = passportMapper.dtoToEntity(requestDto);
        Client client = clientMapper.DtoToEntity(requestDto);
        client.setPassport(passport);

        clientRepository.save(client);

        List<StatusHistoryElementDto> statusHistory = new ArrayList<>();
        statusHistoryManager.addElement(statusHistory, ApplicationStatus.PREAPPROVAL);
        Statement statement = statementMapper.createStatement(statusHistory);
        statement.setClient(client);

        statementRepository.save(statement);

        ResponseEntity<List<LoanOfferDto>> response = externalServiceClient.getResponse(requestDto, "/offers", new ParameterizedTypeReference<>() {
                }
        );

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to fetch loan offers: {}", response.getStatusCode());
            throw new ExternalServiceException("Ошибка со сторонним сервисом", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<LoanOfferDto> loanOffers = response.getBody();

        if (loanOffers != null) {
            for (LoanOfferDto offer : loanOffers) {
                offer.setStatementId(statement.getStatementId());
            }
        } else {
            throw new ExternalServiceException("Пустой ответ от стороннего сервиса", HttpStatus.NO_CONTENT);
        }

        return loanOffers;
    }
}
