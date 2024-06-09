package com.puchkov.calculator.service;

import com.puchkov.calculator.dto.ScoringDataDto;

import java.math.BigDecimal;

public interface ScoringService {

    void score(ScoringDataDto scoringDataDto);

    BigDecimal getRateOnEmployment(ScoringDataDto scoringDataDto);

}
