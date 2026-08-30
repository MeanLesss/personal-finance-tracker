package com.example.coreapi.configuration;

import com.example.common.exception.ResourceNotFoundException;
import com.example.common.util.RestApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(new RestApiResponse<>(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RestApiResponse<Void>> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest()
                .body(new RestApiResponse<>("400", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponse<Void>> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(new RestApiResponse<>("500", "Internal server error"));
    }
}
