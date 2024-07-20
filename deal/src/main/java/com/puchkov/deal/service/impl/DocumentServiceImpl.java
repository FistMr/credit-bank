package com.puchkov.deal.service.impl;

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
        Statement statement = getStatement(statementId);

        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.PREPARE_DOCUMENTS);
        statement.setStatus(ApplicationStatus.PREPARE_DOCUMENTS);
        statementRepository.save(statement);

        kafkaEventsPublisher.sendEventsToTopic(statement, Theme.SEND_DOCUMENTS);
        log.info("DocumentServiceImpl: sendDocument(Exit)");
    }

    @Override
    public void signDocument(UUID statementId) {
        log.info("DocumentServiceImpl: signDocument(Entrance) StatementId = {}", statementId);
        Statement statement = getStatement(statementId);
        //todo генерация ses code и сохранение его в БД
        kafkaEventsPublisher.sendEventsToTopic(statement, Theme.SEND_SES);
        log.info("DocumentServiceImpl: sendDocument(Exit)");
    }

    @Override
    public void codeDocument(UUID statementId) {
        log.info("DocumentServiceImpl: codeDocument(Entrance) StatementId = {}", statementId);
        Statement statement = getStatement(statementId);
        //todo верификация Ses code
        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.DOCUMENT_SIGNED);
        statement.setStatus(ApplicationStatus.DOCUMENT_SIGNED);
        statementRepository.save(statement);
        // что тут должно быть между двумя статусами?
        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.CREDIT_ISSUED);
        statement.setStatus(ApplicationStatus.CREDIT_ISSUED);
        statementRepository.save(statement);

        kafkaEventsPublisher.sendEventsToTopic(statement, Theme.CREDIT_ISSUED);
        log.info("DocumentServiceImpl: sendDocument(Exit)");
    }

    private Statement getStatement(UUID statementId) {
        Optional<Statement> optionalStatement = statementRepository.findById(statementId);
        if (optionalStatement.isEmpty()) {
            throw new DataException("Заявка не существует");
        }
        return optionalStatement.get();
    }

}
