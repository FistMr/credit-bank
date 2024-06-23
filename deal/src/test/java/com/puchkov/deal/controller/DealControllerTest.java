package com.puchkov.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.service.CalculateService;
import com.puchkov.deal.service.OfferService;
import com.puchkov.deal.service.StatementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DealController.class)
class DealControllerTest {

    @MockBean
    private StatementService statementService;

    @MockBean
    private OfferService offerService;

    @MockBean
    private CalculateService calculateService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetOfferDtoList() throws Exception {
        File file = new File("src/test/resources/dto_json/loanStatementRequestDto.json");
        LoanStatementRequestDto requestDto = objectMapper.readValue(file, LoanStatementRequestDto.class);
        List<LoanOfferDto> expectedResponse = new ArrayList<>();
        when(statementService.createClientAndStatement(requestDto))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/deal/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));


        verify(statementService, times(1)).createClientAndStatement(any(LoanStatementRequestDto.class));
    }

    @Test
    void testSaveOffer() throws Exception {
        File file = new File("src/test/resources/dto_json/LoanOfferDto.json");
        LoanOfferDto requestDto = objectMapper.readValue(file, LoanOfferDto.class);

        mockMvc.perform(post("/deal/offer/select")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(offerService, times(1)).saveOffer(any(LoanOfferDto.class));
    }

    @Test
    void testSaveCredit() throws Exception {
        File file = new File("src/test/resources/dto_json/FinishRegistrationRequestDto.json");
        FinishRegistrationRequestDto requestDto = objectMapper.readValue(file, FinishRegistrationRequestDto.class);
        String statementId = UUID.randomUUID().toString();

        mockMvc.perform(post("/deal/calculate/{statementId}", statementId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(calculateService, times(1)).saveCredit(any(FinishRegistrationRequestDto.class), any(UUID.class));
    }
}