package com.puchkov.calculator.service;

import com.puchkov.calculator.config.Properties;
import com.puchkov.calculator.dto.*;
import com.puchkov.calculator.util.InsuranceProcessor;
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

    private final ScoringServiceImpl scoringService;
    private final Properties properties;

    @Override
    public List<LoanOfferDto> getOfferList(LoanStatementRequestDto loanStatementRequestDto) {
        log.info("CalculatorServiceImpl: getOfferList(Entrance) parameters : {}", loanStatementRequestDto);
        List<LoanOfferDto> loanOfferDtoList = new ArrayList<>();

        loanOfferDtoList.add(createOfferDto(loanStatementRequestDto, false, false));
        loanOfferDtoList.add(createOfferDto(loanStatementRequestDto, false, true));
        loanOfferDtoList.add(createOfferDto(loanStatementRequestDto, true, false));
        loanOfferDtoList.add(createOfferDto(loanStatementRequestDto, true, true));

        log.info("CalculatorServiceImpl: getOfferList(exit) response : {}", loanOfferDtoList);
        return loanOfferDtoList;
    }

    private LoanOfferDto createOfferDto(LoanStatementRequestDto requestDto, Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        log.debug("CalculatorServiceImpl: createOfferDto(Entrance) parameters: requestDto = {}, isInsuranceEnasbled = {}, isSalaryClient = {}", requestDto, isInsuranceEnabled, isSalaryClient);

        BigDecimal amount = requestDto.getAmount();
        BigDecimal rate = new BigDecimal(properties.getBaseRate());

        if (isInsuranceEnabled) {
            rate = rate.subtract(BigDecimal.valueOf(3));
            amount = requestDto.getAmount().add(InsuranceProcessor.calculateInsuranceCost(requestDto.getBirthdate(), properties));
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
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
        log.debug("CalculatorServiceImpl: createOfferDto(exit) response: requestDto = {}", loanOfferDto);
        return loanOfferDto;
    }

    @Override
    public CreditDto calcCreditDto(ScoringDataDto scoringDataDto) {
        log.info("CalculatorServiceImpl: calcCreditDto(Entrance) parameters: scoringDataDto = {}", scoringDataDto);

        scoringService.score(scoringDataDto);
        BigDecimal rate = scoringService.getRateOnEmployment(scoringDataDto);

        BigDecimal amount = scoringDataDto.getAmount();

        if (scoringDataDto.getIsInsuranceEnabled()) {
            amount = amount.add(InsuranceProcessor.calculateInsuranceCost(scoringDataDto.getBirthdate(), properties));
            log.debug("CalculatorServiceImpl: calcCreditDto: the amount has been increased scoringDataDto: amount = {}", amount);
        }

        BigDecimal monthlyPayment = MonthlyPaymentProcessor.calculate(amount, scoringDataDto.getTerm(), rate);

        BigDecimal psk = monthlyPayment.multiply(BigDecimal.valueOf(scoringDataDto.getTerm()));

        List<PaymentScheduleElementDto> paymentSchedule = PaymentScheduleProcessor.createPaymentSchedule(scoringDataDto.getTerm(), monthlyPayment, amount, rate);

        CreditDto creditDto = CreditDto.builder()
                .amount(amount.setScale(2, RoundingMode.UP))
                .term(scoringDataDto.getTerm())
                .monthlyPayment(monthlyPayment.setScale(2, RoundingMode.UP))
                .rate(rate.setScale(2, RoundingMode.UP))
                .psk(psk.setScale(2, RoundingMode.UP))
                .isInsuranceEnabled(scoringDataDto.getIsInsuranceEnabled())
                .isSalaryClient(scoringDataDto.getIsSalaryClient())
                .paymentSchedule(paymentSchedule)
                .build();
        log.info("CalculatorServiceImpl: calcCreditDto(exit) response: creditDto = {}", creditDto);
        return creditDto;

    }
}
