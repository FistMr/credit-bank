package com.puchkov.deal.service.main.impl;

import com.puchkov.deal.dto.CreditDto;
import com.puchkov.deal.dto.FinishRegistrationRequestDto;
import com.puchkov.deal.dto.ScoringDataDto;
import com.puchkov.deal.dto.StatusHistoryElementDto;
import com.puchkov.deal.entity.Client;
import com.puchkov.deal.entity.Credit;
import com.puchkov.deal.entity.Employment;
import com.puchkov.deal.entity.Statement;
import com.puchkov.deal.enums.ApplicationStatus;
import com.puchkov.deal.enums.ChangeType;
import com.puchkov.deal.enums.CreditStatus;
import com.puchkov.deal.repository.StatementRepository;
import com.puchkov.deal.service.main.CalclateService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CalculateServiceImpl implements CalclateService {

    private final StatementRepository statementRepository;

    private final RestTemplate restTemplate;

    @Override
    @Transactional
    public void saveCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID statementId) {
        Optional<Statement> optionalStatement = statementRepository.findById(statementId);
        if (optionalStatement.isPresent()) {
            Statement statement = optionalStatement.get();
            Client client = statement.getClient();
            ScoringDataDto scoringDataDto = ScoringDataDto.builder()
                    .amount(statement.getAppliedOffer().getRequestedAmount())
                    .term(statement.getAppliedOffer().getTerm())
                    .firstName(client.getFirstName())
                    .lastName(client.getLastName())
                    .middleName(client.getMiddleName())
                    .gender(finishRegistrationRequestDto.getGender())
                    .birthdate(client.getBirthDate())
                    .passportSeries(client.getPassport().getSeries())
                    .passportNumber(client.getPassport().getNumber())
                    .passportIssueDate(finishRegistrationRequestDto.getPassportIssueDate())
                    .passportIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch())
                    .maritalStatus(finishRegistrationRequestDto.getMaritalStatus())
                    .dependentAmount(finishRegistrationRequestDto.getDependentAmount())
                    .employment(finishRegistrationRequestDto.getEmployment())
                    .accountNumber(finishRegistrationRequestDto.getAccountNumber())
                    .isInsuranceEnabled(statement.getAppliedOffer().getIsInsuranceEnabled())
                    .isSalaryClient(statement.getAppliedOffer().getIsSalaryClient())
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ScoringDataDto> requestEntity = new HttpEntity<>(scoringDataDto, headers);

            ResponseEntity<CreditDto> response = restTemplate.exchange(
                    "http://localhost:8081/calculator/calc",
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<CreditDto>() {
                    }
            );

            CreditDto creditDto = response.getBody(); //todo Optional Nullable

            Credit credit = Credit.builder()
                    .amount(creditDto.getAmount())
                    .term(creditDto.getTerm())
                    .rate(creditDto.getRate())
                    .monthlyPayment(creditDto.getMonthlyPayment())
                    .psk(creditDto.getPsk())
                    .paymentSchedule(creditDto.getPaymentSchedule())
                    .isInsuranceEnabled(creditDto.getIsInsuranceEnabled())
                    .isSalaryClient(creditDto.getIsSalaryClient())
                    .creditStatus(CreditStatus.CALCULATED)
                    .build();

            List<StatusHistoryElementDto> statusHistory = statement.getStatusHistory();
            statusHistory.add(StatusHistoryElementDto.builder()
                    .status(ApplicationStatus.CC_APPROVED)
                    .time(LocalDateTime.now())
                    .changeType(ChangeType.AUTOMATIC)
                    .build());

            Employment employment = Employment.builder()
                    .status(finishRegistrationRequestDto.getEmployment().getEmploymentStatus())
                    .employerINN(finishRegistrationRequestDto.getEmployment().getEmployerINN())
                    .salary(finishRegistrationRequestDto.getEmployment().getSalary())
                    .position(finishRegistrationRequestDto.getEmployment().getPosition())
                    .workExperienceTotal(finishRegistrationRequestDto.getEmployment().getWorkExperienceTotal())
                    .workExperienceCurrent(finishRegistrationRequestDto.getEmployment().getWorkExperienceCurrent())
                    .build();

            client.setEmployment(employment);
            client.setAccountNumber(finishRegistrationRequestDto.getAccountNumber());
            client.setGender(finishRegistrationRequestDto.getGender());
            client.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());
            client.getPassport().setIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch());
            client.getPassport().setIssueDate(finishRegistrationRequestDto.getPassportIssueDate());

            statement.setClient(client);
            statement.setStatus(statusHistory.get(statusHistory.size() - 1).getStatus());
            statement.setStatusHistory(statusHistory);
            statement.setCredit(credit);
            statementRepository.save(statement);
        }
    }
}