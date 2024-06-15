package com.puchkov.deal.service.main.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.ExternalServiceException;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.auxiliary.StatusHistoryManager;
import com.puchkov.deal.service.main.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final StatementRepository statementRepository;

    private final StatusHistoryManager statusHistoryManager;

    @Override
    @Transactional
    public void saveOffer(LoanOfferDto loanOfferDto) {
        Optional<Statement> optionalStatement = statementRepository.findById(loanOfferDto.getStatementId());
        if (optionalStatement.isPresent()) {
            Statement statement = optionalStatement.get();

            List<StatusHistoryElementDto> statusHistory = statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.APPROVED);

            statement.setStatus(ApplicationStatus.APPROVED);
            statement.setStatusHistory(statusHistory);
            statement.setAppliedOffer(loanOfferDto);
            statementRepository.save(statement);
        }else {
            throw new ExternalServiceException("Заявки не существует");
        }
    }
}
