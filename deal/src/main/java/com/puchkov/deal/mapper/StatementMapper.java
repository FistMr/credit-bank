package com.puchkov.deal.mapper;

import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.entity.Statement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class StatementMapper {

    public Statement createStatement(List<StatusHistoryElementDto> statusHistory){
        return Statement.builder()
                .status(statusHistory.get(statusHistory.size() - 1).getStatus())
                .statusHistory(statusHistory)
                .creationDate(LocalDate.now())
                .build();
    }
}
