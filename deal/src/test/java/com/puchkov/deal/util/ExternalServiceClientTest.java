package com.puchkov.deal.util;

import com.puchkov.deal.dto.LoanStatementRequestDto;
import com.puchkov.deal.dto.RequestAbleDto;
import com.puchkov.deal.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ExternalServiceClientTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @Test
    void getResponse_success() {
        RequestAbleDto requestDto = new LoanStatementRequestDto();
        String url = "/offers";
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};

        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(responseType)
        )).thenReturn(responseEntity);

        ResponseEntity<String> response = externalServiceClient.getResponse(requestDto, url, responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());
    }

    @Test
    void getResponse_httpStatusCodeException() {
        RequestAbleDto requestDto = new LoanStatementRequestDto();
        String url = "/offers";
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};

        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getResponseBodyAsString()).thenReturn("Error response");
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(responseType)
        )).thenThrow(exception);

        ExternalServiceException ex = assertThrows(ExternalServiceException.class, () ->
                externalServiceClient.getResponse(requestDto, url, responseType)
        );

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        assertEquals("Error response: Error response", ex.getMessage());
    }

    @Test
    void getResponse_otherException() {
        RequestAbleDto requestDto = new LoanStatementRequestDto();
        String url = "/offers";
        ParameterizedTypeReference<String> responseType = new ParameterizedTypeReference<>() {};

        when(restTemplate.exchange(
                any(String.class),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(responseType)
        )).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<String> response = externalServiceClient.getResponse(requestDto, url, responseType);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}