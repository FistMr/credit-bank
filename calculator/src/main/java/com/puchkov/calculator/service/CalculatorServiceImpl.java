package com.puchkov.calculator.service;

import com.puchkov.calculator.config.ScoringProperties;
import com.puchkov.calculator.dto.*;
import com.puchkov.calculator.util.MonthlyPaymentProcessor;
import com.puchkov.calculator.util.PaymentScheduleProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculatorServiceImpl implements CalculatorService {

    private final ScoringService scoringService;
    private final ScoringProperties scoringProperties;

    @Override
    public List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("CalculatorServiceImpl: getOfferList(Entrance) parameters : {}", loanStatementRequestDto);
        List<LoanOfferDto> loanOfferDtoList = new ArrayList<>();
        Boolean[] insuranceOptions = {false, true};
        Boolean[] salaryClientOptions = {false, true};

        for (boolean isInsuranceEnabled : insuranceOptions) {
            for (boolean isSalaryClient : salaryClientOptions) {
                LoanOfferDto loanOfferDto = createOfferDto(loanStatementRequestDto, isInsuranceEnabled, isSalaryClient);
                log.info("CalculatorServiceImpl: getOfferList addLoanOfferToList loanOfferDtoList : {}", loanOfferDtoList);
                loanOfferDtoList.add(loanOfferDto);
            }
        }
        log.info("CalculatorServiceImpl: getOfferList(exit) response : {}", loanOfferDtoList);
        return loanOfferDtoList;
    }

    private LoanOfferDto createOfferDto(LoanStatementRequestDto requestDto, Boolean isInsuranceEnasbled, Boolean isSalaryClient) {
        log.info("CalculatorServiceImpl: createOfferDto(Entrance) parameters: requestDto = {}" +
                ",isInsuranceEnasbled = {}, isSalaryClient = {}",requestDto,isInsuranceEnasbled,isSalaryClient );
        BigDecimal amount = requestDto.getAmount();
        BigDecimal rate = new BigDecimal(scoringProperties.getBaseRate());

        if (isInsuranceEnasbled) {
            rate = rate.subtract(BigDecimal.valueOf(3));
            amount = requestDto.getAmount().add(BigDecimal.valueOf(100_000));//todo подумать о прогрессивной зависимости
        }

        if (isSalaryClient) {
            rate = rate.subtract(BigDecimal.valueOf(1));
        }

        BigDecimal monthlyPayment = MonthlyPaymentProcessor.calculate(amount, requestDto.getTerm(), rate);
        BigDecimal totalAmount = monthlyPayment.multiply(BigDecimal.valueOf(requestDto.getTerm()));

        LoanOfferDto loanOfferDto = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(requestDto.getAmount())
                .totalAmount(totalAmount.setScale(2, RoundingMode.UP))
                .term(requestDto.getTerm())
                .monthlyPayment(monthlyPayment.setScale(2, RoundingMode.UP))
                .rate(rate)
                .isInsuranceEnabled(isInsuranceEnasbled)
                .isSalaryClient(isSalaryClient)
                .build();
        log.info("CalculatorServiceImpl: createOfferDto(exit) response: requestDto = {}", loanOfferDto);
        return loanOfferDto;
    }

    @Override
    public CreditDto calcCreditDto(ScoringDataDto scoringDataDto) {
        BigDecimal rate = scoringService.score(scoringDataDto);
        BigDecimal amount = scoringDataDto.getAmount();

        if (scoringDataDto.getIsInsuranceEnabled()) {
            amount = amount.add(BigDecimal.valueOf(100_000));//todo подумать о прогрессивной зависимости
        }

        BigDecimal monthlyPayment = MonthlyPaymentProcessor.calculate(amount, scoringDataDto.getTerm(), rate);

        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDto.getTerm()));

        List<PaymentScheduleElementDto> paymentSchedule = PaymentScheduleProcessor.createPaymentSchedule
                (scoringDataDto.getTerm(), monthlyPayment, amount, rate);

        return CreditDto.builder()
                .amount(amount.setScale(2, RoundingMode.UP))
                .term(scoringDataDto.getTerm())
                .monthlyPayment(monthlyPayment.setScale(2, RoundingMode.UP))
                .rate(rate.setScale(2, RoundingMode.UP))
                .psk(psk.setScale(2, RoundingMode.UP))
                .isInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDto.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();

    }
}
