package com.puchkov.deal.service.impl;

import com.puchkov.deal.dto.CreditDto;
import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.ScoringDataDto;
import com.puchkov.deal.entity.Client;
import com.puchkov.deal.entity.Credit;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.exception.ExternalServiceException;
import com.puchkov.deal.mapper.*;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.util.ExternalServiceClient;
import com.puchkov.deal.util.StatusHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

class CalculateServiceImplTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private ScoringDataDtoMapper scoringDataDtoMapper;

    @Mock
    private ExternalServiceClient externalServiceClient;

    @Mock
    private CreditMapper creditMapper;

    @Mock
    private StatusHistoryManager statusHistoryManager;

    @Mock
    private EmploymentMapper employmentMapper;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private PassportMapper passportMapper;

    @InjectMocks
    private CalculateServiceImpl calculateServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveCredit_success() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        Statement statement = new Statement();
        statement.setStatusHistory(new ArrayList<>());
        statement.setClient(new Client());
        statement.setAppliedOffer(new LoanOfferDto());
        ScoringDataDto scoringDataDto = new ScoringDataDto();
        CreditDto creditDto = new CreditDto();
        Credit credit = new Credit();

        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.of(statement));
        when(scoringDataDtoMapper.createDto(any(Statement.class), any(Client.class), any(FinishRegistrationRequestDto.class)))
                .thenReturn(scoringDataDto);
        when(externalServiceClient.getResponse(any(ScoringDataDto.class), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(creditDto, HttpStatus.OK));
        when(creditMapper.dtoToEntity(any(CreditDto.class))).thenReturn(credit);

        calculateServiceImpl.saveCredit(finishRegistrationRequestDto, statementId);

        verify(statusHistoryManager, times(1)).addElement(anyList(), eq(ApplicationStatus.CC_APPROVED));
        verify(statementRepository, times(1)).save(statement);
        assertEquals(ApplicationStatus.CC_APPROVED, statement.getStatus());
        assertEquals(credit, statement.getCredit());
    }

    @Test
    void saveCredit_statementNotFound() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishRegistrationRequestDto = new FinishRegistrationRequestDto();

        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        DataException exception = assertThrows(DataException.class, () -> {
            calculateServiceImpl.saveCredit(finishRegistrationRequestDto, statementId);
        });

        assertEquals("Заявка не существует", exception.getMessage());
        verify(statementRepository, times(0)).save(any(Statement.class));
    }

    @Test
    void saveCredit_offerNotFound() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        Statement statement = new Statement();
        statement.setClient(new Client());
        statement.setStatusHistory(new ArrayList<>());

        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.of(statement));

        DataException exception = assertThrows(DataException.class, () -> {
            calculateServiceImpl.saveCredit(finishRegistrationRequestDto, statementId);
        });

        assertEquals("Оффер не сущуствует", exception.getMessage());
        verify(statementRepository, times(0)).save(any(Statement.class));
    }

    @Test
    void saveCredit_externalServiceUnavailable() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        Statement statement = new Statement();
        statement.setClient(new Client());
        statement.setAppliedOffer(new LoanOfferDto());
        statement.setStatusHistory(new ArrayList<>());
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.of(statement));
        when(scoringDataDtoMapper.createDto(any(Statement.class), any(Client.class), any(FinishRegistrationRequestDto.class)))
                .thenReturn(scoringDataDto);
        when(externalServiceClient.getResponse(any(ScoringDataDto.class), anyString(), any(ParameterizedTypeReference.class)))
                .thenThrow(new ExternalServiceException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            calculateServiceImpl.saveCredit(finishRegistrationRequestDto, statementId);
        });

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
        verify(statusHistoryManager, times(1)).addElement(anyList(), eq(ApplicationStatus.CC_DENIED));
        verify(statementRepository, times(1)).save(statement);
        assertEquals(ApplicationStatus.CC_DENIED, statement.getStatus());
    }

    @Test
    void saveCredit_externalServiceError() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        Statement statement = new Statement();
        statement.setClient(new Client());
        statement.setAppliedOffer(new LoanOfferDto());
        statement.setStatusHistory(new ArrayList<>());
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.of(statement));
        when(scoringDataDtoMapper.createDto(any(Statement.class), any(Client.class), any(FinishRegistrationRequestDto.class)))
                .thenReturn(scoringDataDto);
        when(externalServiceClient.getResponse(any(ScoringDataDto.class), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            calculateServiceImpl.saveCredit(finishRegistrationRequestDto, statementId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        verify(statementRepository, times(0)).save(any(Statement.class));
    }

    @Test
    void saveCredit_emptyResponse() {
        UUID statementId = UUID.randomUUID();
        FinishRegistrationRequestDto finishRegistrationRequestDto = new FinishRegistrationRequestDto();
        Statement statement = new Statement();
        statement.setClient(new Client());
        statement.setAppliedOffer(new LoanOfferDto());
        statement.setStatusHistory(new ArrayList<>());
        ScoringDataDto scoringDataDto = new ScoringDataDto();

        when(statementRepository.findById(any(UUID.class))).thenReturn(Optional.of(statement));
        when(scoringDataDtoMapper.createDto(any(Statement.class), any(Client.class), any(FinishRegistrationRequestDto.class)))
                .thenReturn(scoringDataDto);
        when(externalServiceClient.getResponse(any(ScoringDataDto.class), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            calculateServiceImpl.saveCredit(finishRegistrationRequestDto, statementId);
        });

        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        verify(statementRepository, times(0)).save(any(Statement.class));
    }
}