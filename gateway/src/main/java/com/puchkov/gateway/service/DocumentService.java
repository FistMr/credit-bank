package com.puchkov.gateway.service;

import java.util.UUID;

public interface DocumentService {
    void sendDocument(UUID statementId);

    void signDocument(UUID statementId);

    void codeDocument(UUID statementId);
}
