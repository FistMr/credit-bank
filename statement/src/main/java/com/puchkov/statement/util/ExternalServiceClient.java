package com.puchkov.statement.util;


import com.puchkov.statement.dto.RequestAbleDto;
import com.puchkov.statement.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    public void sendRequest(RequestAbleDto requestDto, String url) {
        log.debug("ExternalServiceClient: sendRequest(Entrance) parameters: requestDto = {}, url = {}", requestDto, url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestAbleDto> requestEntity = new HttpEntity<>(requestDto, headers);
        try {
            restTemplate.postForLocation("http://localhost:8082/deal" + url, requestEntity);
            log.debug("ExternalServiceClient: sendRequest(Exit) Request sent successfully");
        } catch (HttpStatusCodeException e) {
            log.debug("ExternalServiceClient: sendRequest(error) Exception = {}", e.getMessage());
            throw new ExternalServiceException(("Error response: " + e.getResponseBodyAsString()), e.getStatusCode());
        } catch (Exception e) {
            log.debug("ExternalServiceClient: sendRequest(error) Exception = {}", e.getMessage());
            throw new RuntimeException("Error sending request", e);
        }
    }

    public <T> ResponseEntity<T> getResponse(RequestAbleDto requestDto, String url, ParameterizedTypeReference<T> responseType) {
        log.debug("ExternalServiceClient: getResponse(Entrance) parameters : requestDto = {}, url = {}, responseType = {}", requestDto, url, responseType);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestAbleDto> requestEntity = new HttpEntity<>(requestDto, headers);
        try {
            ResponseEntity<T> exchange = restTemplate.exchange(
                    "http://localhost:8082/deal" + url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );
            log.debug("ExternalServiceClient: getResponse(Exit) response = {}", exchange);
            return exchange;
        } catch (HttpStatusCodeException e) {
            log.debug("ExternalServiceClient: getResponse(error) Exception = {}", e.getMessage());
            throw new ExternalServiceException(("Error response: " + e.getResponseBodyAsString()), e.getStatusCode());
        } catch (Exception e) {
            log.debug("ExternalServiceClient: getResponse(error) Exception = {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
