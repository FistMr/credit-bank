package com.puchkov.calculator.util;

import com.puchkov.calculator.dto.PaymentScheduleElementDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@UtilityClass
@Slf4j
public class PaymentScheduleProcessor {

    public List<PaymentScheduleElementDto> createPaymentSchedule(Integer term, BigDecimal monthlyPayment,
                                                                   BigDecimal amount, BigDecimal rate) {
        log.debug("PaymentScheduleProcessor: createPaymentSchedule(Entrance) : parameters: term = {}, monthlyPayment = {}, amount = {}, rate = {}"
                , term,  monthlyPayment,amount,  rate);
        BigDecimal debtPayment = BigDecimal.ZERO;
        BigDecimal remainingDebt = amount;
        List<PaymentScheduleElementDto> paymentSchedule = new ArrayList<>();
        for (int i = 0; i < term; i++) {

            int daysOfMonth = (int) DAYS.between(LocalDate.now().plusMonths(i), LocalDate.now().plusMonths(i + 1));
            int daysOfYear = LocalDate.now().lengthOfYear();

            BigDecimal interestPayment = remainingDebt.multiply(rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.UP))
                    .multiply(BigDecimal.valueOf(daysOfMonth).divide(BigDecimal.valueOf(daysOfYear), 10, RoundingMode.UP));
            debtPayment = debtPayment.add(monthlyPayment.subtract(interestPayment));
            remainingDebt = remainingDebt.subtract(debtPayment);

            if (i + 1 == term) {
                monthlyPayment = monthlyPayment.add(remainingDebt);
                remainingDebt = BigDecimal.ZERO;
            }

            PaymentScheduleElementDto paymentScheduleElementDto = PaymentScheduleElementDto.builder()
                    .number(i + 1)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(monthlyPayment.setScale(2, RoundingMode.UP))
                    .interestPayment(interestPayment.setScale(2, RoundingMode.UP))
                    .debtPayment(debtPayment.setScale(2, RoundingMode.UP))
                    .remainingDebt(remainingDebt.setScale(2, RoundingMode.UP))
                    .build();

            paymentSchedule.add(paymentScheduleElementDto);

            interestPayment = BigDecimal.ZERO;
            debtPayment = BigDecimal.ZERO;

        }
        log.debug("CalculatorServiceImpl: calcCreditDto(exit) response:  paymentSchedule = {}", paymentSchedule);
        return paymentSchedule;
    }
}
