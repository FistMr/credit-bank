package com.puchkov.dossier.service.impl;

import com.puchkov.dossier.dto.EmailMessage;
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

    @KafkaListener(
            topics = {"finish-registration"},
            groupId = "emailGroup",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeEvents(@Payload EmailMessage emailMessage, Acknowledgment acknowledgment) {
        log.info(emailMessage.toString());
        acknowledgment.acknowledge();
    }
}