package com.puchkov.statement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ValidationErrorResponse> handleValidationError(
            MethodArgumentNotValidException validationErrorResponse) {
        final List<Violation> violationList = validationErrorResponse.getBindingResult()
                .getFieldErrors().stream().map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new ValidationErrorResponse(violationList), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {

        Throwable mostSpecificCause = ex.getMostSpecificCause();
        String errorMessage;
        if (mostSpecificCause.toString().contains("DateTimeParseException")) {
            errorMessage = "Неверный формат даты, ожидается формат yyyy-MM-dd.";
        } else {
            String causeMessage = (ex.getCause() != null) ? ex.getCause().toString() : "";
            errorMessage = "Ошибка чтения JSON: " + ex.getMessage().replace(causeMessage, "");
        }
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<CreditRefusal> handleException(
            Exception exception) {
        CreditRefusal incorrectValue = new CreditRefusal();
        incorrectValue.setInfo(exception.getMessage());
        return new ResponseEntity<>(incorrectValue, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
