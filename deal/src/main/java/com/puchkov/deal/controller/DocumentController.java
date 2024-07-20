package com.puchkov.deal.controller;

import com.puchkov.deal.service.DocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/deal/document")
@RequiredArgsConstructor
@Tag(name = "Deal-Document")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/{statementId}/send")
    public void sendDocuments(@PathVariable UUID statementId) {
        documentService.sendDocument(statementId);
    }

    @PostMapping("/{statementId}/sign")
    public void signDocuments(@PathVariable UUID statementId) {
        documentService.signDocument(statementId);
    }

    @PostMapping("/{statementId}/code")
    public void codeDocuments(@PathVariable UUID statementId) {
        documentService.signDocument(statementId);
    }
}
