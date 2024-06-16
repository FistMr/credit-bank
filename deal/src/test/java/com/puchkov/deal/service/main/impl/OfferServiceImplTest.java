package com.puchkov.deal.service.main.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.auxiliary.StatusHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OfferServiceImplTest {

    @Mock
    private StatementRepository statementRepository;

    @Mock
    private StatusHistoryManager statusHistoryManager;

    @InjectMocks
    private OfferServiceImpl offerServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveOffer_success() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);
        Statement statement = new Statement();
        statement.setStatusHistory(new ArrayList<>());

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        offerServiceImpl.saveOffer(loanOfferDto);

        verify(statusHistoryManager, times(1)).addElement(eq(statement.getStatusHistory()), eq(ApplicationStatus.APPROVED));
        verify(statementRepository, times(1)).save(statement);
        assertEquals(ApplicationStatus.APPROVED, statement.getStatus());
        assertEquals(loanOfferDto, statement.getAppliedOffer());
    }

    @Test
    void saveOffer_statementNotFound() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);

        when(statementRepository.findById(statementId)).thenReturn(Optional.empty());

        DataException exception = assertThrows(DataException.class, () -> {
            offerServiceImpl.saveOffer(loanOfferDto);
        });

        assertEquals("Заявки не существует", exception.getMessage());
        verify(statementRepository, times(0)).save(any(Statement.class));
        verify(statusHistoryManager, times(0)).addElement(anyList(), any(ApplicationStatus.class));
    }
}