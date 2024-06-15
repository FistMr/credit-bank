package com.puchkov.deal.service.main.impl;

import com.puchkov.deal.dto.CreditDto;
import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.ScoringDataDto;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.entity.Client;
import com.puchkov.deal.entity.Credit;
import com.puchkov.deal.entity.Employment;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.exception.ExternalServiceException;
import com.puchkov.deal.mapper.*;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.auxiliary.ExternalServiceClient;
import com.puchkov.deal.service.auxiliary.StatusHistoryManager;
import com.puchkov.deal.service.main.CalclateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculateServiceImpl implements CalclateService {

    private final StatementRepository statementRepository;

    private final ScoringDataDtoMapper scoringDataDtoMapper;

    private final ExternalServiceClient externalServiceClient;

    private final CreditMapper creditMapper;

    private final StatusHistoryManager statusHistoryManager;

    private final EmploymentMapper employmentMapper;

    private final ClientMapper clientMapper;

    private final PassportMapper passportMapper;

    @Override
    public void saveCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID statementId) {
        Optional<Statement> optionalStatement = statementRepository.findById(statementId);
        if (optionalStatement.isEmpty()) {
            throw new DataException("Заявка не существует");
        }
        Statement statement = optionalStatement.get();
        Client client = statement.getClient();
        ScoringDataDto scoringDataDto = scoringDataDtoMapper.createDto(statement, client, finishRegistrationRequestDto);
        ResponseEntity<CreditDto> response;
        try {
            response = externalServiceClient.getResponse(scoringDataDto, "/calc", new ParameterizedTypeReference<CreditDto>() {
            });
        } catch (ExternalServiceException e) {
            if (e.getStatus().equals(HttpStatus.SERVICE_UNAVAILABLE)) {
                List<StatusHistoryElementDto> statusHistory = statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.CC_DENIED);
                statement.setStatus(statusHistory.get(statusHistory.size() - 1).getStatus());
                statement.setStatusHistory(statusHistory);
                statementRepository.save(statement);
            }
            throw e;
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to fetch loan offers: {}", response.getStatusCode());
            throw new ExternalServiceException("Ошибка со сторонним сервисом", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CreditDto creditDto = response.getBody(); //todo проверить поля на null

        if (creditDto == null) {
            throw new ExternalServiceException("Пустой ответ от стороннего сервиса", HttpStatus.NO_CONTENT);
        }

        Credit credit = creditMapper.dtoToEntity(creditDto);

        List<StatusHistoryElementDto> statusHistory = statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.CC_APPROVED);

        clientMapper.updateEntity(client, finishRegistrationRequestDto);
        passportMapper.updateEntity(client.getPassport(), finishRegistrationRequestDto);
        Employment employment = employmentMapper.dtoToEntity(finishRegistrationRequestDto);
        client.setEmployment(employment);

        statement.setStatus(statusHistory.get(statusHistory.size() - 1).getStatus());
        statement.setStatusHistory(statusHistory);
        statement.setCredit(credit);
        statementRepository.save(statement);

    }
}