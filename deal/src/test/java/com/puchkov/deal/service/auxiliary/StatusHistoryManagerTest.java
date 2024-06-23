package com.puchkov.deal.service.auxiliary;

import com.puchkov.deal.util.StatusHistoryManager;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatusHistoryManagerTest {

    private final StatusHistoryManager statusHistoryManager = new StatusHistoryManager();

    @Test
    void addElement_success() {
        List<StatusHistoryElementDto> statusHistory = new ArrayList<>();
        ApplicationStatus status = ApplicationStatus.PREAPPROVAL;

        List<StatusHistoryElementDto> result = statusHistoryManager.addElement(statusHistory, status);

        assertEquals(1, result.size());
        StatusHistoryElementDto element = result.get(0);
        assertEquals(status, element.getStatus());
        assertEquals(ChangeType.AUTOMATIC, element.getChangeType());
        assertNotNull(element.getTime());
    }

    @Test
    void addElement_nullStatusHistory() {
        ApplicationStatus status = ApplicationStatus.PREAPPROVAL;

        assertThrows(NullPointerException.class, () -> {
            statusHistoryManager.addElement(null, status);
        });
    }
}