package com.puchkov.dossier.service.impl;

import com.puchkov.dossier.dto.EmailMessage;
import com.puchkov.dossier.util.DocumentService;
import com.puchkov.dossier.util.EmailService;
import com.puchkov.dossier.util.ExternalServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class DossierServiceImpl {

    private final EmailService emailService;

    private final DocumentService documentService;

    private final ExternalServiceClient externalServiceClient;

    @KafkaListener(
            topics = {
                    "statement-denied",
                    "finish-registration",
                    "create-documents",
                    "send-ses",
                    "credit-issued"},
            groupId = "emailGroup",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeStatementDeniedEvents(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) {
        log.info(emailMessage.toString());
        acknowledgment.acknowledge();
        emailService.sendSimpleEmail(emailMessage);
        log.info("Message send to email: {}", emailMessage.getAddress());
    }

    @KafkaListener(
            topics = {"send-documents"},
            groupId = "emailGroup",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeSendDocumentsEvents(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) {
        log.info("Event recived: " + emailMessage.toString());
        acknowledgment.acknowledge();

        externalServiceClient.sendRequest("/deal/admin/statement/" + emailMessage.getStatementId() + "/status");

        try {
            ByteArrayResource resource = documentService.createDocument(emailMessage.getStatementId());
            emailService.sendMessageWithDocuments(emailMessage.getAddress(),
                    emailMessage.getTheme().getTopic(),
                    "Ваши кредитные документы",
                    resource);
        } catch (IOException ex) {
            log.debug("Error creating document");
        } catch (MessagingException | javax.mail.MessagingException ex) {
            log.debug("Error send document");
        }
        log.info("Message send to email: {}", emailMessage.getAddress());
    }
}