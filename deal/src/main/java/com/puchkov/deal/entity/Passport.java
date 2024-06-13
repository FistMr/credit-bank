package com.puchkov.deal.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Passport {
    @Id
    @GeneratedValue
    private UUID passportUuid;

    private String series;
    private String number;
    private String issueBranch;
    private LocalDate issueDate;
}
