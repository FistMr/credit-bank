package com.puchkov.calculator.service.impl;

import com.puchkov.calculator.config.Properties;
import com.puchkov.calculator.dto.ScoringDataDto;
import com.puchkov.calculator.dto.enums.Gender;
import com.puchkov.calculator.exception.ScoringException;
import com.puchkov.calculator.service.ScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringServiceImpl implements ScoringService {

    private final Properties scoringProperties;

    public void score(ScoringDataDto scoringDataDto) {
        log.debug("ScoringService: score(Entrance) : parameters: scoringDataDto = {}", scoringDataDto);

        int minCreditAge = Integer.parseInt(scoringProperties.getMinCreditAge());
        int maxCreditAge = Integer.parseInt(scoringProperties.getMaxCreditAge());
        int minWorkExperienceCurrent = Integer.parseInt(scoringProperties.getMinWorkExperienceCurrent());
        int minWorkExperienceTotal = Integer.parseInt(scoringProperties.getMinWorkExperienceTotal());
        int maxCreditToSalaryRatio = Integer.parseInt(scoringProperties.getMaxCreditToSalaryRatio());

        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {
            case UNEMPLOYED:
            case RETIRED:
            case STUDENT:
                throw new ScoringException("Отказ в выдаче кредита: неудовлетврительный рабочий статус");
        }

        log.debug("ScoringService: score : EmploymentStatus isValid");

        if (scoringDataDto.getAmount().compareTo(scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(maxCreditToSalaryRatio))) > 0) {
            throw new ScoringException("Отказ в выдаче кредита: Сумма кредита больше чем 25 зарплат заемщика");
        }

        log.debug("ScoringService: score : Salary isValid ");

        if (scoringDataDto.getBirthdate().plus(Period.ofYears(minCreditAge)).isAfter(LocalDate.now())
                || scoringDataDto.getBirthdate().plus(Period.ofYears(maxCreditAge)).isBefore(LocalDate.now())) {
            throw new ScoringException("Отказ в выдаче кредита: неудовлетврительный возраст");
        }

        log.debug("ScoringService: score : Birthdate isValid");

        if (scoringDataDto.getEmployment().getWorkExperienceTotal() < minWorkExperienceTotal
                || scoringDataDto.getEmployment().getWorkExperienceCurrent() < minWorkExperienceCurrent) {
            throw new ScoringException("Отказ в выдаче кредита: неудовлетврительный стаж работы");
        }

        log.debug("ScoringService: score : WorkExperienceTotal isValid");
    }


    public BigDecimal getRateOnEmployment(ScoringDataDto scoringDataDto) {
        log.debug("ScoringService: getRateOnEmployment(Entrance) : parameters: scoringDataDto = {}", scoringDataDto);
        BigDecimal rate = new BigDecimal(scoringProperties.getBaseRate());

        int minFemaleRateAge = Integer.parseInt(scoringProperties.getMinFemaleRateAge());
        int maxFemaleRateAge= Integer.parseInt(scoringProperties.getMaxFemaleRateAge());
        int minMaleRateAge= Integer.parseInt(scoringProperties.getMinMaleRateAge());
        int maxMaleRateAge= Integer.parseInt(scoringProperties.getMaxMaleRateAge());

        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            case BUSINESSMAN:
                rate = rate.add(BigDecimal.valueOf(2));
                break;
        }

        log.debug("ScoringService: score : EmploymentStatus isValid rate = {}", rate);

        switch (scoringDataDto.getEmployment().getPosition()) {

            case FREELANCER:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            case MID_LEVEL:
            case SENIOR_LEVEL:
                rate = rate.subtract(BigDecimal.valueOf(1));
                break;
            case MANAGER:
                rate = rate.subtract(BigDecimal.valueOf(2));
                break;
            case DIRECTOR:
            case OWNER:
                rate = rate.subtract(BigDecimal.valueOf(3));

        }

        log.debug("ScoringService: score : Position isValid rate = {}", rate);

        switch (scoringDataDto.getMaritalStatus()) {
            case DIVORCED:
            case WIDOWED:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            case MARRIED:
                rate = rate.subtract(BigDecimal.valueOf(3));
        }

        log.debug("ScoringService: score : MaritalStatus isValid rate = {}", rate);

        if (Objects.equals(Gender.FEMALE,scoringDataDto.getGender())
                && (scoringDataDto.getBirthdate().plus(Period.ofYears(minFemaleRateAge)).isBefore(LocalDate.now())
                && scoringDataDto.getBirthdate().plus(Period.ofYears(maxFemaleRateAge)).isAfter(LocalDate.now()))) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }


        if (Objects.equals(Gender.MALE,scoringDataDto.getGender())
                && (scoringDataDto.getBirthdate().plus(Period.ofYears(minMaleRateAge)).isBefore(LocalDate.now())
                && scoringDataDto.getBirthdate().plus(Period.ofYears(maxMaleRateAge)).isAfter(LocalDate.now()))) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }

        log.debug("ScoringService: score : Gender$Birthdate isValid rate = {}", rate);

        if (scoringDataDto.getIsInsuranceEnabled()) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }

        log.debug("ScoringService: score : IsInsuranceEnabled isValid rate = {}", rate);

        if (scoringDataDto.getIsSalaryClient()) {
            rate = rate.subtract(BigDecimal.valueOf(1));
        }

        log.debug("ScoringService: score : IsSalaryClient isValid rate = {}", rate);

        log.debug("ScoringService: score : response = {}", rate);
        return rate;
    }

}
