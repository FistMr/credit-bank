package com.puchkov.deal.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.puchkov.deal.dto.PaymentScheduleElementDto;
import com.puchkov.deal.enums.CreditStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Credit {
    @Id
    @GeneratedValue
    private UUID creditId;

    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;

    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private List<PaymentScheduleElementDto> paymentSchedule;

    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;

    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}
