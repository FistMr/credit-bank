package com.puchkov.deal.util;

import org.springframework.stereotype.Service;

@Service
public class VerifyingService {

    public boolean verify(int clientSesCode, int serviceSesCode) {
        return clientSesCode == serviceSesCode;
    }
}
