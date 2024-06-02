package com.puchkov.calculator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puchkov.calculator.dto.*;
import com.puchkov.calculator.dto.enums.EmploymentStatus;
import com.puchkov.calculator.dto.enums.Gender;
import com.puchkov.calculator.dto.enums.MaritalStatus;
import com.puchkov.calculator.dto.enums.Position;
import com.puchkov.calculator.service.CalculatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
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
        LoanStatementRequestDto requestDto = new LoanStatementRequestDto();
        requestDto.setFirstName("Pavel");
        requestDto.setMiddleName("Puchkov");
        requestDto.setLastName("Ilyich");
        requestDto.setEmail("ivanov@example.com");
        requestDto.setBirthdate(LocalDate.of(1998, 1, 1));
        requestDto.setAmount(BigDecimal.valueOf(50000));
        requestDto.setTerm(12);
        requestDto.setPassportSeries("1234");
        requestDto.setPassportNumber("567890");
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
        ScoringDataDto requestDto = ScoringDataDto.builder()
                .amount(new BigDecimal("300000"))
                .term(12)
                .firstName("Pavel")
                .lastName("Ilyich")
                .middleName("Puchkov")
                .gender(Gender.MALE)
                .birthdate(LocalDate.of(2001, 11, 6))
                .passportSeries("1234")
                .passportNumber("567890")
                .passportIssueDate(LocalDate.of(2019, 7, 11))
                .passportIssueBranch("УМВД России по г. Севастополю")
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(2)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .employerINN("123456789012")
                        .salary(new BigDecimal("60000"))
                        .position(Position.MANAGER)
                        .workExperienceTotal(25)
                        .workExperienceCurrent(12)
                        .build())
                .accountNumber("1234567890")
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();
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