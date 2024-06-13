package com.puchkov.deal.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import com.puchkov.deal.enums.CreditStatus;
import lombok.Data;

@Entity
@Data
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
    private String paymentSchedule;
    private boolean insuranceEnabled;
    private boolean salaryClient;

    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;
}
