package com.puchkov.statement.util;

import com.puchkov.statement.dto.LoanOfferDto;
import com.puchkov.statement.dto.RequestAbleDto;
import com.puchkov.statement.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ExternalServiceClientTest {

    @Autowired
    private ExternalServiceClient externalServiceClient;

    @MockBean
    private RestTemplate restTemplate;


    @Test
    void sendRequestShouldSendRequestSuccessfully() {
        RequestAbleDto requestDto = new LoanOfferDto();

        externalServiceClient.sendRequest(requestDto, "/offer/select");

        ArgumentCaptor<HttpEntity<RequestAbleDto>> requestCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate, times(1)).postForLocation(eq("http://deal:8082/deal" + "/offer/select"), requestCaptor.capture());
        assertEquals(requestDto, requestCaptor.getValue().getBody());
    }

    @Test
    void sendRequestShouldThrowExternalServiceExceptionOnHttpError() {
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getResponseBodyAsString()).thenReturn("Error");
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        doThrow(exception).when(restTemplate).postForLocation(anyString(), any(HttpEntity.class));

        ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> externalServiceClient.sendRequest(new LoanOfferDto(), "/offer/select")
        );

        assertEquals("Error response: Error", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void sendRequest_ShouldThrowRuntimeExceptionOnGeneralException() {
        doThrow(new RuntimeException("error")).when(restTemplate).postForLocation(anyString(), any(HttpEntity.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> externalServiceClient.sendRequest(new LoanOfferDto(), "/offer/select")
        );

        assertEquals("Error sending request", thrown.getMessage());
        assertEquals("error", thrown.getCause().getMessage());
    }


    @Test
    void getResponseShouldReturnResponseSuccessfully() {
        List<LoanOfferDto> responseBody = List.of(new LoanOfferDto());
        ResponseEntity<List<LoanOfferDto>> expectedResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                eq("http://deal:8082/deal" + "/statement"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))
        ).thenReturn(expectedResponse);

        ResponseEntity<List<LoanOfferDto>> response = externalServiceClient.getResponse(new LoanOfferDto(), "/statement", new ParameterizedTypeReference<>() {
        });

        assertEquals(expectedResponse, response);
    }

    @Test
    void getResponseShouldThrowExternalServiceExceptionOnHttpError() {
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getResponseBodyAsString()).thenReturn("Error");
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))
        ).thenThrow(exception);

        ExternalServiceException thrown = assertThrows(
                ExternalServiceException.class,
                () -> externalServiceClient.getResponse(new LoanOfferDto(), "/statement", new ParameterizedTypeReference<>() {
                })
        );

        assertEquals("Error response: Error", thrown.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
    }

    @Test
    void getResponseShouldReturnInternalServerErrorOnException() {

        when(restTemplate.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(ParameterizedTypeReference.class))
        ).thenThrow(new RuntimeException());

        ResponseEntity<String> response = externalServiceClient.getResponse(new LoanOfferDto(), "/statement", new ParameterizedTypeReference<>() {
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}