package com.puchkov.calculator.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void handleScoringExceptionTest() {
        ScoringException exception = new ScoringException("Scoring failed");
        ResponseEntity<CreditRefusal> response = globalExceptionHandler.handleScoringException(exception);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE),
                () -> assertThat(response.getBody().getInfo()).isEqualTo("Scoring failed")
        );
    }

    @Test
    void handleValidationErrorTest() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(null, "ScoringDataDTO");
        bindingResult.addError(new FieldError("ScoringDataDTO", "firstName", "must not be blank"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ValidationErrorResponse> response = globalExceptionHandler.handleValidationError(ex);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody().getViolationList()).hasSize(1),
                () -> assertThat(response.getBody().getViolationList().get(0).getFieldName()).isEqualTo("firstName"),
                () -> assertThat(response.getBody().getViolationList().get(0).getMessage()).isEqualTo("must not be blank")
        );
    }

    @Test
    public void handleHttpMessageNotReadableExceptionTest() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        when(ex.getMostSpecificCause()).thenReturn(new Throwable("DateTimeParseException"));

        ResponseEntity<Object> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                () -> assertThat(response.getBody()).isEqualTo("Неверный формат даты, ожидается формат yyyy-MM-dd.")
        );
    }
}