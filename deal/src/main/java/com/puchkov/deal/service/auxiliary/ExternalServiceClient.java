package com.puchkov.deal.service.auxiliary;


import com.puchkov.deal.dto.RequestAble;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExternalServiceClient {

    private final RestTemplate restTemplate;

    public <T> ResponseEntity<T> getResponse(RequestAble requestDto, String url, ParameterizedTypeReference<T> responseType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RequestAble> requestEntity = new HttpEntity<>(requestDto, headers);

        return restTemplate.exchange(
                "http://localhost:8081/calculator" + url,
                HttpMethod.POST,
                requestEntity,
                responseType
        );
    }
}
