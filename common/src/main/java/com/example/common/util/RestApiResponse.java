package com.example.common.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestApiResponse<T> {
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Pagination pagination;

    public RestApiResponse() {
        this.code = "0000";
        this.message = "OK";
    }

    public RestApiResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public RestApiResponse(T data) {
        this.code = "0000";
        this.message = "OK";
        this.data = data;
    }

    public RestApiResponse(T data, Pagination pagination) {
        this.code = "0000";
        this.message = "OK";
        this.data = data;
        this.pagination = pagination;
    }

    public RestApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public RestApiResponse(String code, String message, T data, Pagination pagination) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    public RestApiResponse(String code, T data) {
        this.code = code;
        this.data = data;
    }
}