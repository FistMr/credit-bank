package com.puchkov.gateway.service.impl;

import com.puchkov.gateway.service.DocumentService;
import com.puchkov.gateway.util.ExternalServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final ExternalServiceClient externalServiceClient;

    @Override
    public void sendDocument(UUID statementId) {
        externalServiceClient.sendRequest("http://localhost:8082/deal/document/" + statementId + "/send");
    }

    @Override
    public void signDocument(UUID statementId) {
        externalServiceClient.sendRequest("http://localhost:8082/deal/document/" + statementId + "/sign");
    }

    @Override
    public void codeDocument(UUID statementId) {
        externalServiceClient.sendRequest("http://localhost:8082/deal/document/" + statementId + "/code");
    }
}
