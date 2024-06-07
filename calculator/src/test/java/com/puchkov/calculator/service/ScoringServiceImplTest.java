package com.puchkov.calculator.service;

import com.puchkov.calculator.CalculatorApplication;
import com.puchkov.calculator.config.Properties;
import com.puchkov.calculator.dto.EmploymentDto;
import com.puchkov.calculator.dto.ScoringDataDto;
import com.puchkov.calculator.dto.enums.EmploymentStatus;
import com.puchkov.calculator.dto.enums.Gender;
import com.puchkov.calculator.dto.enums.MaritalStatus;
import com.puchkov.calculator.dto.enums.Position;
import com.puchkov.calculator.exception.ScoringException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = {CalculatorApplication.class})
class ScoringServiceImplTest {

    @Mock
    private Properties scoringProperties;

    @Autowired
    private ScoringService scoringService;

    @BeforeEach
    void setUp() {

        lenient().when(scoringProperties.getBaseRate()).thenReturn("10");
        lenient().when(scoringProperties.getBaseInsuranceCost()).thenReturn("100000");
        lenient().when(scoringProperties.getMinCreditAge()).thenReturn("20");
        lenient().when(scoringProperties.getMaxCreditAge()).thenReturn("65");
        lenient().when(scoringProperties.getMinWorkExperienceCurrent()).thenReturn("3");
        lenient().when(scoringProperties.getMinWorkExperienceTotal()).thenReturn("18");
        lenient().when(scoringProperties.getMaxFemaleRateAge()).thenReturn("60");
        lenient().when(scoringProperties.getMinFemaleRateAge()).thenReturn("32");
        lenient().when(scoringProperties.getMaxMaleRateAge()).thenReturn("55");
        lenient().when(scoringProperties.getMinMaleRateAge()).thenReturn("30");

    }

    @Test
    void testScoreWithUnacceptableEmploymentStatusThrowsException() {

        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.UNEMPLOYED)
                        .build())
                .build();

        assertThrows(ScoringException.class, () -> scoringService.score(scoringDataDto));
    }

    @Test
    void testScoreWithCreditAmountExceedingLimitThrowsException() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .amount(new BigDecimal("260000"))
                .birthdate(LocalDate.now().minusYears(30))
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .salary(new BigDecimal("10000"))
                        .workExperienceCurrent(6)
                        .workExperienceTotal(18)
                        .build())
                .build();
        assertThrows(ScoringException.class, () -> scoringService.score(scoringDataDto));
    }

    @Test
    void testScoreWithInvalidAgeThrowsException() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .amount(new BigDecimal("240000"))
                .birthdate(LocalDate.now().minusYears(19))
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .salary(new BigDecimal("10000"))
                        .workExperienceCurrent(6)
                        .workExperienceTotal(18)
                        .build())
                .build();
        assertThrows(ScoringException.class, () -> scoringService.score(scoringDataDto));
    }

    @Test
    void testScoreWithUnacceptableWorkExperienceThrowsException() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .amount(new BigDecimal("100000"))
                .term(6)
                .gender(Gender.MALE)
                .birthdate(LocalDate.now().minusYears(20))
                .maritalStatus(MaritalStatus.SINGLE)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .salary(new BigDecimal("10000"))
                        .workExperienceTotal(18)
                        .workExperienceCurrent(2)
                        .build())
                .build();

        assertThrows(ScoringException.class, () -> scoringService.score(scoringDataDto));
    }

    @Test
    void testGetRateOnEmploymentWithSelfEmployedAndDirectorDecreasesRate() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .birthdate(LocalDate.now().minusYears(19))
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.SELF_EMPLOYED)
                        .position(Position.DIRECTOR)
                        .build())
                .build();

        BigDecimal rate = scoringService.getRateOnEmployment(scoringDataDto);

        assertEquals(new BigDecimal("8.0"), rate);
    }

    @Test
    void testGetRateOnEmploymentWithInsuranceAndFreelancerDecreasesRate() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .birthdate(LocalDate.now().minusYears(20))
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .isInsuranceEnabled(true)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .position(Position.FREELANCER)
                        .build())
                .build();

        BigDecimal rate = scoringService.getRateOnEmployment(scoringDataDto);

        assertEquals(new BigDecimal("8.0"), rate);
    }

    @Test
    void testGetRateOnEmploymentWithSeniorAndBusinessmanAndMarriedDecreasesRate() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .birthdate(LocalDate.now().minusYears(20))
                .maritalStatus(MaritalStatus.MARRIED)
                .gender(Gender.MALE)
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.BUSINESSMAN)
                        .position(Position.SENIOR_LEVEL)
                        .build())
                .build();

        BigDecimal rate = scoringService.getRateOnEmployment(scoringDataDto);

        assertEquals(new BigDecimal("8.0"), rate);
    }

    @Test
    void testGetRateOnEmploymentWithManagerAndWidowedAndRateAgeDecreasesRate() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .birthdate(LocalDate.now().minusYears(33))
                .maritalStatus(MaritalStatus.WIDOWED)
                .gender(Gender.FEMALE)
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .position(Position.MANAGER)
                        .build())
                .build();

        BigDecimal rate = scoringService.getRateOnEmployment(scoringDataDto);

        assertEquals(new BigDecimal("6.0"), rate);
    }

    @Test
    void testGetRateOnEmploymentRateAgeAndSalaryClientDecreasesRate() {
        ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                .birthdate(LocalDate.now().minusYears(35))
                .maritalStatus(MaritalStatus.SINGLE)
                .gender(Gender.MALE)
                .isInsuranceEnabled(false)
                .isSalaryClient(true)
                .employment(EmploymentDto.builder()
                        .employmentStatus(EmploymentStatus.EMPLOYEE)
                        .position(Position.ENTRY_LEVEL)
                        .build())
                .build();

        BigDecimal rate = scoringService.getRateOnEmployment(scoringDataDto);

        assertEquals(new BigDecimal("6.0"), rate);
    }
}