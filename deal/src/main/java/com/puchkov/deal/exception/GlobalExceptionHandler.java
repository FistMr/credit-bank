package com.puchkov.deal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<IncorrectValue> handleExternalServiceException(
            ExternalServiceException exception) {
        IncorrectValue incorrectValue = new IncorrectValue();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.BAD_GATEWAY);
    }

}
