package com.puchkov.deal.service;

import com.puchkov.deal.dto.FinishRegistrationRequestDto;

import java.util.UUID;

public interface CalclateService {

    void saveCredit(FinishRegistrationRequestDto finishRegistrationRequestDto, UUID statementId);

}
