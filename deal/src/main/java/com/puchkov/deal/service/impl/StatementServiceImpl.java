package com.puchkov.deal.service.impl;

import com.puchkov.deal.dto.LoanOfferDto;
import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.entity.Client;
import com.puchkov.deal.entity.Passport;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import com.puchkov.deal.repository.ClientRepository;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.StatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StatementServiceImpl implements StatementService {

    private final ClientRepository clientRepository;

    private final StatementRepository statementRepository;

    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public List<LoanOfferDto> createClientAndStatement(LoanStatementRequestDto requestDto) {
        Passport passport = Passport.builder()
                .series(requestDto.getPassportSeries())
                .number(requestDto.getPassportNumber())
                .build();
        Client client = Client.builder() //todo сделать mapper-s
                .lastName(requestDto.getLastName())
                .firstName(requestDto.getFirstName())
                .middleName(requestDto.getMiddleName())
                .birthDate(requestDto.getBirthdate())
                .email(requestDto.getEmail())
                .passport(passport)
                .build();
        clientRepository.save(client);

        List<StatusHistoryElementDto> statusHistory = new ArrayList<>(); //todo использовать другую коллекцию
        statusHistory.add(StatusHistoryElementDto.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDateTime.now())
                .changeType(ChangeType.AUTOMATIC)
                .build());

        LoanOfferDto appliedOffer = LoanOfferDto.builder()
                .statementId(UUID.randomUUID())
                .isSalaryClient(false)
                .isInsuranceEnabled(false)
                .monthlyPayment(BigDecimal.ZERO)
                .rate(BigDecimal.ZERO)
                .requestedAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO)
                .term(0)
                .build();

        Statement statement = Statement.builder()
                .client(client)
                .status(statusHistory.get(statusHistory.size() - 1).getStatus())
                .statusHistory(statusHistory)
                .creationDate(LocalDate.now())
                .appliedOffer(appliedOffer)
                .build();

        statementRepository.save(statement);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoanStatementRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        ResponseEntity<List<LoanOfferDto>> response = restTemplate.exchange(
                "http://localhost:8081/calculator/offers",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<LoanOfferDto>>() {
                }
        );

        List<LoanOfferDto> loanOffers = response.getBody();

        if (loanOffers != null) {
            for (LoanOfferDto offer : loanOffers) {
                offer.setStatementId(statement.getStatementId());
            }
        }

        return loanOffers;
    }
}
