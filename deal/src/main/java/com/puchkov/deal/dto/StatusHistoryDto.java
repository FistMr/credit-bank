package com.puchkov.deal.dto;

import com.puchkov.deal.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistoryDto {

    private String status;
    private LocalDate time;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
}
