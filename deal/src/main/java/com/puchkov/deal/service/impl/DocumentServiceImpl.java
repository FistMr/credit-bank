package com.puchkov.deal.service.impl;

import com.puchkov.deal.dto.EmailMessage;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.Theme;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.DocumentService;
import com.puchkov.deal.util.KafkaEventsPublisher;
import com.puchkov.deal.util.StatusHistoryManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final StatementRepository statementRepository;

    private final StatusHistoryManager statusHistoryManager;

    private final KafkaEventsPublisher kafkaEventsPublisher;

    @Override
    public void sendDocument(UUID statementId) {
        log.info("DocumentServiceImpl: sendDocument(Entrance) StatementId = {}", statementId);
        Optional<Statement> optionalStatement = statementRepository.findById(statementId);
        if (optionalStatement.isEmpty()) {
            throw new DataException("Заявка не существует");
        }
        Statement statement = optionalStatement.get();

        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.PREPARE_DOCUMENTS);
        statement.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
        statementRepository.save(statement);

        kafkaEventsPublisher.sendEventsToTopic(EmailMessage.builder()
                .address(statement.getClient().getEmail())
                .theme(Theme.CREATE_DOCUMENTS)
                .statementId(statement.getStatementId())
                .build());
        log.info("DocumentServiceImpl: sendDocument(Exit)");
    }

    @Override
    public void signDocument(UUID statementId) {
        log.info("DocumentServiceImpl: signDocument(Entrance) StatementId = {}", statementId);
        Optional<Statement> optionalStatement = statementRepository.findById(statementId);
        if (optionalStatement.isEmpty()) {
            throw new DataException("Заявка не существует");
        }
        Statement statement = optionalStatement.get();

        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.DOCUMENT_SIGNED);
        statement.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        statementRepository.save(statement);

        kafkaEventsPublisher.sendEventsToTopic(EmailMessage.builder()
                .address(statement.getClient().getEmail())
                .theme(Theme.SEND_SES)
                .statementId(statement.getStatementId())
                .build());
        log.info("DocumentServiceImpl: sendDocument(Exit)");
    }
}
