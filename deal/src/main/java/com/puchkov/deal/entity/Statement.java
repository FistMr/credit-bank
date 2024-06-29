package com.puchkov.deal.entity;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.enums.ApplicationStatus;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data //todo Data+Entity проверить
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Statement {
    @Id
    @GeneratedValue
    private UUID statementId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDate creationDate;

    @Column(columnDefinition = "jsonb")
    @Type(type = "jsonb")
    private LoanOfferDto appliedOffer;

    private LocalDate signDate;

    private String sesCode;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<StatusHistoryElementDto> statusHistory;
}
