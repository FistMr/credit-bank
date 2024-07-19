package com.puchkov.deal.service;

import java.util.UUID;

public interface DocumentService {

    void sendDocument(UUID statementId);

    void signDocument(UUID statementId);
}
