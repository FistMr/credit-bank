package com.puchkov.calculator.service;

import com.puchkov.calculator.config.ScoringProperties;
import com.puchkov.calculator.dto.ScoringDataDto;
import com.puchkov.calculator.dto.enums.Gender;
import com.puchkov.calculator.exception.ScoringException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final ScoringProperties scoringProperties;

    public BigDecimal score(ScoringDataDto scoringDataDto) {
        log.info("ScoringService: score(Entrance) : parameters: scoringDataDto = {}", scoringDataDto);
        BigDecimal rate = new BigDecimal(scoringProperties.getBaseRate());

        switch (scoringDataDto.getEmployment().getEmploymentStatus()) {
            case SELF_EMPLOYED:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            case BUSINESSMAN:
                rate = rate.add(BigDecimal.valueOf(2));
                break;
            case UNEMPLOYED:
            case RETIRED:
            case STUDENT:
                throw new ScoringException("Отказ в выдаче кредита: неудовлетврительный рабочий статус");
        }

        log.info("ScoringService: score : EmploymentStatus isValid rate = {}", rate);

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

        log.info("ScoringService: score : Position isValid rate = {}", rate);

        switch (scoringDataDto.getMaritalStatus()) {
            case DIVORCED:
            case WIDOWED:
                rate = rate.add(BigDecimal.valueOf(1));
                break;
            case MARRIED:
                rate = rate.subtract(BigDecimal.valueOf(3));
        }

        log.info("ScoringService: score : MaritalStatus isValid rate = {}", rate);

        if (scoringDataDto.getAmount().compareTo(scoringDataDto.getEmployment().getSalary().multiply(BigDecimal.valueOf(25.0))) > 0) {
            throw new ScoringException("Отказ в выдаче кредита: Сумма кредита больше чем 25 зарплат заемщика");
        }

        log.info("ScoringService: score : Salary isValid rate = {}", rate);

        if (scoringDataDto.getBirthdate().plus(Period.ofYears(20)).isAfter(LocalDate.now())
                || scoringDataDto.getBirthdate().plus(Period.ofYears(65)).isBefore(LocalDate.now())) {
            throw new ScoringException("Отказ в выдаче кредита: неудовлетврительный возраст");
        }

        log.info("ScoringService: score : Birthdate isValid rate = {}", rate);

        if (scoringDataDto.getGender().equals(Gender.FEMALE)
                && (scoringDataDto.getBirthdate().plus(Period.ofYears(32)).isAfter(LocalDate.now())
                && scoringDataDto.getBirthdate().plus(Period.ofYears(60)).isBefore(LocalDate.now()))) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }


        if (scoringDataDto.getGender().equals(Gender.MALE)
                && (scoringDataDto.getBirthdate().plus(Period.ofYears(30)).isAfter(LocalDate.now())
                && scoringDataDto.getBirthdate().plus(Period.ofYears(55)).isBefore(LocalDate.now()))) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }

        log.info("ScoringService: score : Gender$Birthdate isValid rate = {}", rate);

        if (scoringDataDto.getEmployment().getWorkExperienceTotal() < 18
                || scoringDataDto.getEmployment().getWorkExperienceCurrent() < 3) {
            throw new ScoringException("Отказ в выдаче кредита: неудовлетврительный стаж работы");
        }

        log.info("ScoringService: score : WorkExperienceTotal isValid rate = {}", rate);

        if (scoringDataDto.getIsInsuranceEnabled()) {
            rate = rate.subtract(BigDecimal.valueOf(3));
        }

        log.info("ScoringService: score : IsInsuranceEnabled isValid rate = {}", rate);

        if (scoringDataDto.getIsSalaryClient()) {
            rate = rate.subtract(BigDecimal.valueOf(1));
        }

        log.info("ScoringService: score : IsSalaryClient isValid rate = {}", rate);

        log.info("ScoringService: score : response = {}", rate);
        return rate;
    }

}
