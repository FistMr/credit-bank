package com.puchkov.deal.service.impl;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.puchkov.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DealServiceImpl implements DealService {

    private final ClientRepository clientRepository;

    private final StatementRepository statementRepository;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public List<LoanOfferDto> createClientAndStatement(LoanStatementRequestDto requestDto) {
        Passport passport = Passport.builder()
                .series(requestDto.getPassportSeries())
                .number(requestDto.getPassportNumber())
                .build();
        Client client = Client.builder()
                .lastName(requestDto.getLastName())
                .firstName(requestDto.getFirstName())
                .middleName(requestDto.getMiddleName())
                .birthDate(requestDto.getBirthdate())
                .email(requestDto.getEmail())
                .passport(passport)
                .build();
        clientRepository.save(client);

        List<StatusHistoryElementDto> statusHistory = new ArrayList<>();//todo использовать другую коллекцию
        statusHistory.add(StatusHistoryElementDto.builder()
                .status(ApplicationStatus.PREAPPROVAL)
                .time(LocalDate.now())
                .changeType(ChangeType.AUTOMATIC)
                .build());
        Statement statement = Statement.builder()
                .client(client)
                .status(statusHistory.get(statusHistory.size() - 1).getStatus())
                //.statusHistory(statusHistory.toString()) //todo сделать конвертер string в json
                .creationDate(LocalDate.now())
                .build();
        statementRepository.save(statement);

        // Подготовка тела запроса и заголовков
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoanStatementRequestDto> requestEntity = new HttpEntity<>(requestDto, headers);

        // Запрос к /calculator/offers
        ResponseEntity<List<LoanOfferDto>> response = restTemplate.exchange(
                "http://localhost:8081/calculator/offers",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<LoanOfferDto>>() {
                }
        );

        List<LoanOfferDto> loanOffers = response.getBody();

        // Назначение statementId
        if (loanOffers != null) {
            for (LoanOfferDto offer : loanOffers) {
                offer.setStatementId(statement.getStatementId());
            }
        }


        return loanOffers;
    }
}
