package com.puchkov.deal.service.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StatementServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private PassportMapper passportMapper;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private StatusHistoryManager statusHistoryManager;

    @Mock
    private StatementMapper statementMapper;

    @Mock
    private ExternalServiceClient externalServiceClient;

    @InjectMocks
    private StatementServiceImpl statementServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createClientAndStatement_success() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        Passport passport = new Passport();
        Client client = new Client();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());

        when(passportMapper.dtoToEntity(any())).thenReturn(passport);
        when(clientMapper.DtoToEntity(any())).thenReturn(client);
        when(statementMapper.createStatement(any())).thenReturn(statement);

        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        when(externalServiceClient.getResponse(any(), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<LoanOfferDto> result = statementServiceImpl.createClientAndStatement(requestDto);

        assertNotNull(result);
        verify(clientRepository, times(1)).save(client);
        verify(statementRepository, times(1)).save(statement);
        verify(statusHistoryManager, times(1)).addElement(anyList(), eq(ApplicationStatus.PREAPPROVAL));
        verify(externalServiceClient, times(1)).getResponse(any(), anyString(), any(ParameterizedTypeReference.class));
    }

    @Test
    void createClientAndStatement_externalServiceError() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        Passport passport = new Passport();
        Client client = new Client();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());

        when(passportMapper.dtoToEntity(any())).thenReturn(passport);
        when(clientMapper.DtoToEntity(any())).thenReturn(client);
        when(statementMapper.createStatement(any())).thenReturn(statement);

        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        when(externalServiceClient.getResponse(any(), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            statementServiceImpl.createClientAndStatement(requestDto);
        });

        assertEquals("Ошибка со сторонним сервисом", exception.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        verify(clientRepository, times(1)).save(client);
        verify(statementRepository, times(1)).save(statement);
        verify(statusHistoryManager, times(1)).addElement(anyList(), eq(ApplicationStatus.PREAPPROVAL));
        verify(externalServiceClient, times(1)).getResponse(any(), anyString(), any(ParameterizedTypeReference.class));
    }

    @Test
    void createClientAndStatement_emptyResponse() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        Passport passport = new Passport();
        Client client = new Client();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());

        when(passportMapper.dtoToEntity(any())).thenReturn(passport);
        when(clientMapper.DtoToEntity(any())).thenReturn(client);
        when(statementMapper.createStatement(any())).thenReturn(statement);

        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        when(externalServiceClient.getResponse(any(), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        ExternalServiceException exception = assertThrows(ExternalServiceException.class, () -> {
            statementServiceImpl.createClientAndStatement(requestDto);
        });

        assertEquals("Пустой ответ от стороннего сервиса", exception.getMessage());
        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        verify(clientRepository, times(1)).save(client);
        verify(statementRepository, times(1)).save(statement);
        verify(statusHistoryManager, times(1)).addElement(anyList(), eq(ApplicationStatus.PREAPPROVAL));
        verify(externalServiceClient, times(1)).getResponse(any(), anyString(), any(ParameterizedTypeReference.class));
    }
}