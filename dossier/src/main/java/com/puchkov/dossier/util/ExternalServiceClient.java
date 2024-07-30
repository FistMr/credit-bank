package com.puchkov.dossier.util;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    public void sendRequest(String url) {
        log.debug("ExternalServiceClient: sendRequest(Entrance) parameters: url = {}", url);

        try {
            restTemplate.exchange(url, HttpMethod.PUT, HttpEntity.EMPTY, Void.class);
            log.debug("ExternalServiceClient: sendRequest(Exit) Request sent successfully");
        } catch (Exception e) {
            log.debug("ExternalServiceClient: sendRequest(error) Exception = {}", e.getMessage());
        }
    }
}
