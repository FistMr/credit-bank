package com.puchkov.calculator.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<CreditRefusal> handleScoringException(
            ScoringException exception) {
        CreditRefusal incorrectValue = new CreditRefusal();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.SERVICE_UNAVAILABLE);//todo проверить код ответа
    }

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleValidationError(
            MethodArgumentNotValidException validationErrorResponse){
        final List<Violation> violationList =validationErrorResponse.getBindingResult()
                .getFieldErrors().stream().map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ValidationErrorResponse(violationList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<CreditRefusal> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException httpMessageNotReadableException){
        CreditRefusal incorrectValue = new CreditRefusal();
        incorrectValue.setInfo(httpMessageNotReadableException.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.BAD_REQUEST);
    }

}
