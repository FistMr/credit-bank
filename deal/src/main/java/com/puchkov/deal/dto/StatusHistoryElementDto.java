package com.puchkov.deal.dto;

import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusHistoryElementDto implements Serializable {

    private ApplicationStatus status;
    private LocalDateTime time;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;
}
