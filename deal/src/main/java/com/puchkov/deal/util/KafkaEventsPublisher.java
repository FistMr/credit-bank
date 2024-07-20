package com.puchkov.deal.util;

import com.puchkov.deal.dto.EmailMessage;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.Theme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventsPublisher {

    private final KafkaTemplate<String, Object> template;

    public void sendEventsToTopic(EmailMessage emailMessage) {
        log.debug("KafkaEventsPublisher: sendEventsToTopic(Entrance) parameters : {}", emailMessage);
        String topic = emailMessage.getTheme().getTopic();
        try {
            ListenableFuture<SendResult<String, Object>> future = template.send(topic, UUID.randomUUID().toString(), emailMessage);
            future.addCallback(
                    result -> log.info("Sent message=[{}] with offset=[{}]", emailMessage, result.getRecordMetadata().offset()),
                    ex -> log.error("Unable to send message=[{}] due to : {}", emailMessage, ex.getMessage())
            );

        } catch (Exception ex) {
            System.out.println("ERROR : " + ex.getMessage());
        }
    }

    public void sendEventsToTopic(Statement statement) {
        sendEventsToTopic(EmailMessage.builder()
                .address(statement.getClient().getEmail())
                .theme(Theme.SEND_SES)
                .statementId(statement.getStatementId())
                .build());
    }
}
