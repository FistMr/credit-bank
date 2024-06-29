package com.puchkov.deal.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private StatementRepository statementRepository;

    @MockBean
    private PassportMapper passportMapper;

    @MockBean
    private ClientMapper clientMapper;

    @MockBean
    private StatusHistoryManager statusHistoryManager;

    @MockBean
    private StatementMapper statementMapper;

    @MockBean
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private StatementService statementService;

    @Test
    void createClientAndStatement_success() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        Client client = new Client();
        Statement statement = new Statement();
        statement.setStatementId(UUID.randomUUID());

        when(passportMapper.dtoToEntity(any())).thenReturn(new Passport());
        when(clientMapper.DtoToEntity(any())).thenReturn(client);
        when(statementMapper.createStatement(any())).thenReturn(statement);

        ResponseEntity<List<LoanOfferDto>> responseEntity = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        when(externalServiceClient.getResponse(any(), anyString(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        List<LoanOfferDto> result = statementService.createClientAndStatement(requestDto);

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
            statementService.createClientAndStatement(requestDto);
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
            statementService.createClientAndStatement(requestDto);
        });

        assertEquals("Пустой ответ от стороннего сервиса", exception.getMessage());
        assertEquals(HttpStatus.NO_CONTENT, exception.getStatus());
        verify(clientRepository, times(1)).save(client);
        verify(statementRepository, times(1)).save(statement);
        verify(statusHistoryManager, times(1)).addElement(anyList(), eq(ApplicationStatus.PREAPPROVAL));
        verify(externalServiceClient, times(1)).getResponse(any(), anyString(), any(ParameterizedTypeReference.class));
    }
}