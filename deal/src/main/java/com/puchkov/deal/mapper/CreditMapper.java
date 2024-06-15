package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.CreditDto;
import com.puchkov.deal.entity.Credit;
import com.puchkov.deal.enums.CreditStatus;
import org.springframework.stereotype.Component;

@Component
public class CreditMapper {

    public Credit dtoToEntity(CreditDto creditDto){
        return Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .rate(creditDto.getRate())
                .monthlyPayment(creditDto.getMonthlyPayment())
                .psk(creditDto.getPsk())
                .paymentSchedule(creditDto.getPaymentSchedule())
                .isInsuranceEnabled(creditDto.getIsInsuranceEnabled())
                .isSalaryClient(creditDto.getIsSalaryClient())
                .creditStatus(CreditStatus.CALCULATED)
                .build();
    }

}
