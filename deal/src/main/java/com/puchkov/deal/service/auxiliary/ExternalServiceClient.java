package com.puchkov.deal.service.auxiliary;


import com.puchkov.deal.dto.RequestAbleDto;
import com.puchkov.deal.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> getResponse(RequestAbleDto requestDto, String url, ParameterizedTypeReference<T> responseType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestAbleDto> requestEntity = new HttpEntity<>(requestDto, headers);
        try {
            return restTemplate.exchange(
                    "http://localhost:8081/calculator" + url,
                    HttpMethod.POST,
                    requestEntity,
                    responseType
            );
        } catch (HttpStatusCodeException e) {
            throw new ExternalServiceException(("Error response: " + e.getResponseBodyAsString()), e.getStatusCode());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
