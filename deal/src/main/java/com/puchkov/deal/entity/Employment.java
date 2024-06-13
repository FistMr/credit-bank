package com.puchkov.deal.entity;

import com.puchkov.deal.enums.EmploymentStatus;
import com.puchkov.deal.enums.Position;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Employment {
    @Id
    @GeneratedValue
    private UUID employmentUuid;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus status;

    private String employerInn;
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    private Position position;

    private int workExperienceTotal;
    private int workExperienceCurrent;
}
