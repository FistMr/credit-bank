package com.puchkov.deal.service;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.Theme;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.util.KafkaEventsPublisher;
import com.puchkov.deal.util.StatusHistoryManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @MockBean
    private StatementRepository statementRepository;

    @MockBean
    private StatusHistoryManager statusHistoryManager;

    @MockBean
    private KafkaEventsPublisher kafkaEventsPublisher;

    @Autowired
    private OfferService offerService;

    @Test
    void saveOffer_success() {
        UUID statementId = UUID.randomUUID();
        LoanOfferDto loanOfferDto = new LoanOfferDto();
        loanOfferDto.setStatementId(statementId);
        Statement statement = new Statement();
        statement.setStatusHistory(new ArrayList<>());

        when(statementRepository.findById(statementId)).thenReturn(Optional.of(statement));

        Mockito.doNothing().when(kafkaEventsPublisher).sendEventsToTopic(statement, Theme.FINISH_REGISTRATION);
        offerService.saveOffer(loanOfferDto);

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
            offerService.saveOffer(loanOfferDto);
        });

        assertEquals("Заявки не существует", exception.getMessage());
        verify(statementRepository, times(0)).save(any(Statement.class));
        verify(statusHistoryManager, times(0)).addElement(anyList(), any(ApplicationStatus.class));
    }
}