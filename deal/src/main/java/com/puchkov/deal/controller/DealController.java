package com.puchkov.deal.controller;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.service.CalculateService;
import com.puchkov.deal.service.OfferService;
import com.puchkov.deal.service.StatementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
@Tag(name = "Deal")
public class DealController {

    private final StatementService statementService;

    private final OfferService offerService;

    private final CalculateService calculateService;

    @PostMapping("/statement")
    public List<LoanOfferDto> getOfferDtoList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return statementService.createClientAndStatement(loanStatementRequestDto);
    }

    @PostMapping("/offer/select")
    public void saveOffer(@Valid @RequestBody LoanOfferDto loanOfferDto) {
        offerService.saveOffer(loanOfferDto);
    }

    @PostMapping("/calculate/{statementId}")
    public void saveCredit(@Valid @RequestBody FinishRegistrationRequestDto finishRegistrationRequestDto,
                           @PathVariable String statementId) {
        calculateService.saveCredit(finishRegistrationRequestDto, UUID.fromString(statementId));
    }
}
