package com.example.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    public ResourceNotFoundException(String message) {
        super(message);
        this.code = "404";
        this.status = HttpStatus.NOT_FOUND;
    }

    public ResourceNotFoundException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
