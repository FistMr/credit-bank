package com.puchkov.deal.service.main.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.exception.DataException;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.auxiliary.StatusHistoryManager;
import com.puchkov.deal.service.main.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final StatementRepository statementRepository;

    private final StatusHistoryManager statusHistoryManager;

    @Override
    @Transactional
    public void saveOffer(LoanOfferDto loanOfferDto) {
        log.info("OfferServiceImpl: saveOffer(Entrance) parameters : {}", loanOfferDto);
        Optional<Statement> optionalStatement = statementRepository.findById(loanOfferDto.getStatementId());
        if (optionalStatement.isEmpty()) {
            throw new DataException("Заявки не существует");
        }
        Statement statement = optionalStatement.get();

        statusHistoryManager.addElement(statement.getStatusHistory(), ApplicationStatus.APPROVED);
        statement.setStatus(ApplicationStatus.APPROVED);
        statement.setAppliedOffer(loanOfferDto);

        statementRepository.save(statement);
        log.info("OfferServiceImpl: saveOffer(Exit)");
    }
}
