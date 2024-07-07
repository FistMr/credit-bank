package com.puchkov.statement.service;

import com.puchkov.statement.dto.LoanOfferDto;
import com.puchkov.statement.dto.LoanStatementRequestDto;
import com.puchkov.statement.util.ExternalServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

    @MockBean
    private ExternalServiceClient externalServiceClient;

    @Autowired
    private StatementService statementService;

    @Test
    void getOfferList_ShouldReturnListOfLoanOffers() {
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        List<LoanOfferDto> expectedOffers = List.of(new LoanOfferDto());

        when(externalServiceClient.getResponse(eq(requestDto), eq("/statement"), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(expectedOffers));

        List<LoanOfferDto> result = statementService.getOfferList(requestDto);

        assertEquals(expectedOffers, result);
        verify(externalServiceClient, times(1)).getResponse(eq(requestDto), eq("/statement"), any(ParameterizedTypeReference.class));
    }

    @Test
    void getCreditDto_ShouldSendRequest() {
        LoanOfferDto loanOfferDto = new LoanOfferDto();

        statementService.getCreditDto(loanOfferDto);

        ArgumentCaptor<LoanOfferDto> captor = ArgumentCaptor.forClass(LoanOfferDto.class);
        verify(externalServiceClient, times(1)).sendRequest(captor.capture(), eq("/offer/select"));
        assertEquals(loanOfferDto, captor.getValue());
    }
}