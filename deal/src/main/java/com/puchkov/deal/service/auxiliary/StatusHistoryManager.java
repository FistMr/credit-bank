package com.puchkov.deal.service.auxiliary;

import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatusHistoryManager {

    public List<StatusHistoryElementDto> addElement(List<StatusHistoryElementDto> statusHistory, ApplicationStatus status) {
        StatusHistoryElementDto newElement = StatusHistoryElementDto.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();
        statusHistory.add(newElement);
        return statusHistory;

    }
}
