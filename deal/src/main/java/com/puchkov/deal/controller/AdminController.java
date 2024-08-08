package com.puchkov.deal.controller;

import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/deal/admin/statement")
@RequiredArgsConstructor
@Tag(name = "Deal-Admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/{statementId}")
    public Statement getStatement(@PathVariable UUID statementId) {
        return adminService.getStatement(statementId);
    }

    @GetMapping
    public List<Statement> getAllStatement() {
        return adminService.getAllStatement();
    }

    @PutMapping("/{statementId}/status")
    public void updateApplicationStatus(@PathVariable UUID statementId) {
        adminService.updateApplicationStatusToDocumentsCreated(statementId);
    }

}
