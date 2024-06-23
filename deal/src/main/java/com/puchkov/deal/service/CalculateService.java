package com.puchkov.deal.service;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;

import java.util.UUID;

public interface CalculateService {

    void saveCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID statementId);

}
