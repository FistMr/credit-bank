package com.puchkov.dossier.enums;

import lombok.Getter;

@Getter
public enum Theme {
    FINISH_REGISTRATION("finish-registration"),
    CREATE_DOCUMENTS("create-documents"),
    SEND_DOCUMENTS("send-documents"),
    SEND_SES("send-ses"),
    CREDIT_ISSUED("credit-issued"),
    STATEMENT_DENIED("statement-denied");

    private final String topic;

    Theme(String topic) {
        this.topic = topic;
    }

}
