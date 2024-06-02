package com.puchkov.calculator.service;

import com.puchkov.calculator.config.Properties;
import com.puchkov.calculator.dto.*;
import com.puchkov.calculator.dto.enums.EmploymentStatus;
import com.puchkov.calculator.dto.enums.Gender;
import com.puchkov.calculator.dto.enums.MaritalStatus;
import com.puchkov.calculator.dto.enums.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    void getOfferList() {
        LoanStatementRequestDto request = LoanStatementRequestDto.builder().amount(new BigDecimal("50000.00")).term(12).firstName("Pavel").lastName("Ilyich").middleName("Puchkov").email("pavel.pu4ckow@yandex.ru").birthdate(LocalDate.parse("2001-11-06")).passportSeries("1234").passportNumber("123456").build();

        List<LoanOfferDto> offers = calculatorService.getOfferList(request);

        assertEquals(4, offers.size());

        assertTrue(offers.stream().anyMatch(o -> o.getIsInsuranceEnabled() && o.getIsSalaryClient()));
        assertTrue(offers.stream().anyMatch(o -> o.getIsInsuranceEnabled() && !o.getIsSalaryClient()));
        assertTrue(offers.stream().anyMatch(o -> !o.getIsInsuranceEnabled() && o.getIsSalaryClient()));
        assertTrue(offers.stream().anyMatch(o -> !o.getIsInsuranceEnabled() && !o.getIsSalaryClient()));
    }

    @Test
    void testCalcCreditDto() {

        ScoringDataDto scoringData = ScoringDataDto.builder().amount(new BigDecimal("50000.00")).term(12).firstName("Pavel").lastName("Ilyich").middleName("Puchkov").gender(Gender.MALE).birthdate(LocalDate.of(2001, 11, 6)).passportSeries("1234").passportNumber("567890").passportIssueDate(LocalDate.of(2019, 7, 11)).passportIssueBranch("УМВД России по г. Севастополю").maritalStatus(MaritalStatus.SINGLE).dependentAmount(0).employment(EmploymentDto.builder().employmentStatus(EmploymentStatus.STUDENT).employerINN("1234567890").salary(new BigDecimal("60000.00")).position(Position.MANAGER).workExperienceTotal(5).workExperienceCurrent(2).build()).accountNumber("1234567890123456").isInsuranceEnabled(true).isSalaryClient(true).build();


        when(scoringService.getRateOnEmployment(any())).thenReturn(new BigDecimal("10"));

        CreditDto credit = calculatorService.calcCreditDto(scoringData);

        assertNotNull(credit);
        assertEquals(new BigDecimal("72000.00"), credit.getAmount());
        assertEquals(12, credit.getTerm());
        assertNotNull(credit.getPaymentSchedule());
        assertFalse(credit.getPaymentSchedule().isEmpty());
        assertTrue(credit.getIsInsuranceEnabled());
        assertTrue(credit.getIsSalaryClient());
        verify(scoringService).score(scoringData);
    }
}