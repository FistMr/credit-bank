package com.puchkov.statement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puchkov.statement.dto.LoanOfferDto;
import com.puchkov.statement.dto.LoanStatementRequestDto;
import com.puchkov.statement.service.StatementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatementController.class)
class StatementControllerTest {

    @MockBean
    private StatementService statementService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetOfferDtoList() throws Exception {
        File file = new File("src/test/resources/dto_json/LoanStatementRequestDto.json");
        LoanStatementRequestDto requestDto = objectMapper.readValue(file, LoanStatementRequestDto.class);
        List<LoanOfferDto> expectedResponse = new ArrayList<>();
        when(statementService.getOfferList(requestDto))
                .thenReturn(expectedResponse);

        mockMvc.perform(post("/statement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));


        verify(statementService, times(1)).getOfferList(any(LoanStatementRequestDto.class));
    }

    @Test
    void testSaveOffer() throws Exception {
        File file = new File("src/test/resources/dto_json/LoanOfferDto.json");
        LoanOfferDto requestDto = objectMapper.readValue(file, LoanOfferDto.class);

        mockMvc.perform(post("/statement/offer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        verify(statementService, times(1)).getCreditDto(any(LoanOfferDto.class));
    }
}