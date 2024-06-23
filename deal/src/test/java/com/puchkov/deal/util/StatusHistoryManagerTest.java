package com.puchkov.deal.util;

import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class StatusHistoryManagerTest {

    @Autowired
    StatusHistoryManager statusHistoryManager;

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