package com.puchkov.dossier.service.impl;

import com.puchkov.dossier.dto.EmailMessage;
import com.puchkov.dossier.util.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DossierServiceImpl {

    private final EmailService emailService;

    @KafkaListener(
            topics = {
                    "finish-registration",
                    "create-documents",
                    "send-documents",
                    "send-ses",
                    "credit-issued",
                    "statement-denied"},
            groupId = "emailGroup",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeEvents(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) {
        log.info(emailMessage.toString());
        acknowledgment.acknowledge();
        //emailService.sendSimpleEmail(emailMessage); //todo не работает отправка писем(проблема с логином или паролем)
    }
}