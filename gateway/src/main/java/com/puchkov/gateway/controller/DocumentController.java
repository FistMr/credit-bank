package com.puchkov.gateway.controller;

import com.puchkov.gateway.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
@Tag(name = "Gateway-Document")
public class DocumentController {

    private final DocumentService documentService;

    @Operation(summary = "Запрос на создание документов")
    @PostMapping("/{statementId}")
    public void sendDocuments(@PathVariable UUID statementId) {
        documentService.sendDocument(statementId);
    }

    @Operation(summary = "Запрос на подписание документов")
    @PostMapping("/{statementId}/sign")
    public void signDocuments(@PathVariable UUID statementId) {
        documentService.signDocument(statementId);
    }

    @Operation(summary = "Верификация подписи")
    @PostMapping("/{statementId}/code")
    public void codeDocuments(@PathVariable UUID statementId) {
        documentService.codeDocument(statementId);
    }
}
