package com.puchkov.deal.controller;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.service.main.CalclateService;
import com.puchkov.deal.service.main.OfferService;
import com.puchkov.deal.service.main.StatementService;
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

    private final CalclateService calclateService;

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
        calclateService.saveCredit(finishRegistrationRequestDto, UUID.fromString(statementId));
    }
}
