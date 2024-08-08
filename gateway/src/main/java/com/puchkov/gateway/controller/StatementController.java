package com.puchkov.gateway.controller;

import com.puchkov.gateway.dto.FinishRegistrationRequestDto;
import com.puchkov.gateway.dto.LoanOfferDto;
import com.puchkov.gateway.dto.LoanStatementRequestDto;
import com.puchkov.gateway.service.StatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("statement")
@Tag(name = "Gateway-Statement")
@Slf4j
public class StatementController {

    private  final StatementService statementService;

    @Operation(summary = "расчёт возможных условий кредита")
    @PostMapping
    public List<LoanOfferDto> getOfferDtoList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return statementService.getOfferList(loanStatementRequestDto);
    }

    @Operation(summary = "валидация присланных данных + скоринг данных + полный расчет параметров кредита")
    @PostMapping("/select")
    public void saveOffer(@Valid @RequestBody LoanOfferDto loanOfferDto) {
        statementService.getCreditDto(loanOfferDto);
    }

    @Operation(summary = "завершение регистрации кредита")
    @PostMapping("/registration/{statementId}")
    public void saveCredit(@Valid @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                           @PathVariable String statementId) {
        statementService.saveCredit(finishRegistrationRequestDto, UUID.fromString(statementId));
    }

}
