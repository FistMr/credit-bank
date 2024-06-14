package com.puchkov.deal.controller;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.service.OfferService;
import com.puchkov.deal.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController {

    private final StatementService statementService;

    private final OfferService offerService;

    @PostMapping("/statement")
    public List<LoanOfferDto> getOfferDtoList(@Valid @RequestBody LoanStatementRequestDto loanStatementRequestDto) {
        return statementService.createClientAndStatement(loanStatementRequestDto);
    }

    @PostMapping("/offer/select")
    public void saveOffer(@Valid @RequestBody LoanOfferDto loanOfferDto) {
        offerService.saveOffer(loanOfferDto);
    }
}
