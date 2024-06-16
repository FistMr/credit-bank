package com.puchkov.deal.service.auxiliary;

import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class StatusHistoryManager {

    public List<StatusHistoryElementDto> addElement(List<StatusHistoryElementDto> statusHistory, ApplicationStatus status) {
        log.debug("StatusHistoryManager: addElement(Entrance) parameters : statusHistory = {}, status = {}", statusHistory, status);
        StatusHistoryElementDto newElement = StatusHistoryElementDto.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build();
        statusHistory.add(newElement);
        log.debug("StatusHistoryManager: addElement(Exit) parameters : statusHistory = {}",statusHistory);
        return statusHistory;
    }
}
