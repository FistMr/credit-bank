package com.puchkov.calculator.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.puchkov.calculator.config.Properties;
import com.puchkov.calculator.dto.CreditDto;
import com.puchkov.calculator.dto.LoanOfferDto;
import com.puchkov.calculator.dto.LoanStatementRequestDto;
import com.puchkov.calculator.dto.ScoringDataDto;
import com.puchkov.calculator.service.impl.CalculatorServiceImpl;
import com.puchkov.calculator.service.impl.ScoringServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculatorServiceImplTest {

    @Mock
    private ScoringServiceImpl scoringService;

    @Mock
    private Properties properties;

    @InjectMocks
    private CalculatorServiceImpl calculatorService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @BeforeEach
    public void setup() {
        lenient().when(properties.getBaseRate()).thenReturn("5");
        lenient().when(properties.getBaseInsuranceCost()).thenReturn("100000");
        lenient().when(properties.getMinCreditAge()).thenReturn("20");
        lenient().when(properties.getMaxCreditAge()).thenReturn("65");
        lenient().when(properties.getMinWorkExperienceCurrent()).thenReturn("18");
        lenient().when(properties.getMinWorkExperienceTotal()).thenReturn("3");
    }

    @Test
    void getOfferList() throws IOException {
        File file = new File("src/test/resources/dto_json/loanStatementRequestDto.json");
        LoanStatementRequestDto request = objectMapper.readValue(file, LoanStatementRequestDto.class);

        List<LoanOfferDto> offers = calculatorService.getOfferList(request);

        assertAll(
                () -> assertEquals(4, offers.size()),
                () -> assertTrue(offers.stream().anyMatch(o -> o.getIsInsuranceEnabled() && o.getIsSalaryClient())),
                () -> assertTrue(offers.stream().anyMatch(o -> o.getIsInsuranceEnabled() && !o.getIsSalaryClient())),
                () -> assertTrue(offers.stream().anyMatch(o -> !o.getIsInsuranceEnabled() && o.getIsSalaryClient())),
                () -> assertTrue(offers.stream().anyMatch(o -> !o.getIsInsuranceEnabled() && !o.getIsSalaryClient())));

    }

    @Test
    void testCalcCreditDto() throws IOException {
        File file = new File("src/test/resources/dto_json/scoringDataDto.json");
        ScoringDataDto scoringData = objectMapper.readValue(file, ScoringDataDto.class);

        when(scoringService.getRateOnEmployment(any())).thenReturn(new BigDecimal("10"));

        CreditDto credit = calculatorService.calcCreditDto(scoringData);

        assertAll(
                () -> assertNotNull(credit),
                () -> assertEquals(new BigDecimal("52000.00"), credit.getAmount()),
                () -> assertEquals(6, credit.getTerm()),
                () -> assertNotNull(credit.getPaymentSchedule()),
                () -> assertFalse(credit.getPaymentSchedule().isEmpty()),
                () -> assertTrue(credit.getIsInsuranceEnabled()),
                () -> assertTrue(credit.getIsSalaryClient())
        );

        verify(scoringService).score(scoringData);
    }
}