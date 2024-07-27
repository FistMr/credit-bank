package com.puchkov.gateway.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class ValidationErrorResponse {

    private final List<Violation> violationList;

}

