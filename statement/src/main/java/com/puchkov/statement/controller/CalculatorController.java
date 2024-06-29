package com.puchkov.statement.controller;

import com.puchkov.statement.dto.LoanOfferDto;
import com.puchkov.statement.dto.LoanStatementRequestDto;
import com.puchkov.statement.service.StatementService;
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
@RequestMapping("statement")
@Tag(name = "Calculator")
@Slf4j
public class CalculatorController {

    private final StatementService statementService;

    @Operation(summary = "расчёт возможных условий кредита")
    @PostMapping("")
    public List<LoanOfferDto> getOfferDtoList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return statementService.getOfferList(loanStatementRequestDto);

    }

    @Operation(summary = "валидация присланных данных + скоринг данных + полный расчет параметров кредита")
    @PostMapping("/offer")
    public void getCreditDto(@Valid @RequestBody LoanOfferDto loanOfferDto) {
        statementService.getCreditDto(loanOfferDto);
    }
}
