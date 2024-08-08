package com.puchkov.deal.service;

import com.puchkov.deal.entity.Statement;

import java.util.List;
import java.util.UUID;

public interface AdminService {

    Statement getStatement(UUID statementId);

    List<Statement> getAllStatement();

    void updateApplicationStatusToDocumentsCreated(UUID statementId);

}
