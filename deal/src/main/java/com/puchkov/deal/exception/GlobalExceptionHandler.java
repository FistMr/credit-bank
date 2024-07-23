package com.puchkov.deal.exception;

import com.puchkov.deal.util.VerifyingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleValidationError(
            MethodArgumentNotValidException validationErrorResponse) {
        final List<Violation> violationList = validationErrorResponse.getBindingResult()
                .getFieldErrors().stream().map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ValidationErrorResponse(violationList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<IncorrectValue> handleExternalServiceException(
            ExternalServiceException exception) {
        IncorrectValue incorrectValue = new IncorrectValue();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler
    public ResponseEntity<IncorrectValue> handleExternalServiceException(
            VerifyingException exception) {
        IncorrectValue incorrectValue = new IncorrectValue();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.PRECONDITION_FAILED);
    }

    @ExceptionHandler
    public ResponseEntity<IncorrectValue> handleExternalServiceException(
            DataException exception) {
        IncorrectValue incorrectValue = new IncorrectValue();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler
    public ResponseEntity<IncorrectValue> handleExternalServiceScoringException(
            HttpServerErrorException exception) {
        IncorrectValue incorrectValue = new IncorrectValue();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.SERVICE_UNAVAILABLE);
    }

}
