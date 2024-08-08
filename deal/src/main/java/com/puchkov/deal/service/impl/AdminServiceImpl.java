package com.puchkov.deal.service.impl;

import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.AdminService;
import com.puchkov.deal.util.StatusHistoryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final StatementRepository statementRepository;
    private final StatusHistoryManager statusHistoryManager;

    @Override
    public Statement getStatement(UUID statementId) {
        Optional<Statement> optionalStatement = statementRepository.findById(statementId);
        if (optionalStatement.isEmpty()) {
            throw new DataException("Заявка не существует");
        }
        return optionalStatement.get();
    }

    @Override
    public List<Statement> getAllStatement() {
        return statementRepository.findAll();
    }

    @Override
    public void updateApplicationStatusToDocumentsCreated(UUID statementId) {
        Statement statement = getStatement(statementId);
        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.DOCUMENT_CREATED);
        statement.setStatus(ApplicationStatus.DOCUMENT_CREATED);
        statementRepository.save(statement);
    }
}
