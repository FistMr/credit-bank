package com.puchkov.calculator.controller;

import com.puchkov.calculator.dto.CreditDto;
import com.puchkov.calculator.dto.LoanOfferDto;
import com.puchkov.calculator.dto.LoanStatementRequestDto;
import com.puchkov.calculator.dto.ScoringDataDto;
import com.puchkov.calculator.service.CalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("calculator")
@Tag(name="Calculator")
@Slf4j
public class CalculatorController {

    private final CalculatorService calculatorService;

    @Operation(
            summary = "расчёт возможных условий кредита",
            description = "Request - LoanStatementRequestDto, response - List<LoanOfferDto>"
    )
    @PostMapping("/offers")
    public List<LoanOfferDto> getOfferDtoList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto){
        log.info("CalculatorController: getOfferDtoList request: {}",loanStatementRequestDto);
        List<LoanOfferDto> offerList = calculatorService.getOfferList(loanStatementRequestDto);
        log.info("CalculatorController: getOfferDtoList response : {}", offerList);
        return offerList;
    }
    @Operation(
            summary = "валидация присланных данных + скоринг данных + полный расчет параметров кредита",
            description = "Request - ScoringDataDto, response CreditDto."
    )
    @PostMapping("/calc")
    public CreditDto getCreditDto(@Valid @RequestBody ScoringDataDto scoringDataDto){
        return calculatorService.calcCreditDto(scoringDataDto);
    }
}
