package com.puchkov.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puchkov.calculator.dto.CreditDto;
import com.puchkov.calculator.dto.LoanOfferDto;
import com.puchkov.calculator.dto.LoanStatementRequestDto;
import com.puchkov.calculator.dto.ScoringDataDto;
import com.puchkov.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculatorController.class)
public class CalculatorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalculatorService calculatorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getOfferDtoListTest() throws Exception {
        File file = new File("src/test/resources/dto_json/loanStatementRequestDto.json");
        LoanStatementRequestDto requestDto = objectMapper.readValue(file, LoanStatementRequestDto.class);
        List<LoanOfferDto> expectedResponse = new ArrayList<>();

        when(calculatorService.getOfferList(requestDto)).thenReturn(expectedResponse);

        mockMvc.perform(post("/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(calculatorService, times(1)).getOfferList(requestDto);
    }

    @Test
    void getCreditDtoTest() throws Exception {
        File file = new File("src/test/resources/dto_json/scoringDataDto.json");
        ScoringDataDto requestDto = objectMapper.readValue(file, ScoringDataDto.class);
        CreditDto expectedResponse = new CreditDto();

        when(calculatorService.calcCreditDto(requestDto)).thenReturn(expectedResponse);

        mockMvc.perform(post("/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        verify(calculatorService, times(1)).calcCreditDto(requestDto);
    }
}