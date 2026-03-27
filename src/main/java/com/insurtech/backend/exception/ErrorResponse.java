package com.insurtech.backend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @JsonProperty("status") int status,
        @JsonProperty("code") String error,
        @JsonProperty("message") String description,
        @JsonProperty("path") String path,
        @JsonProperty("timestamp") Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String description, String path) {
        return new ErrorResponse(status, error, description, path, Instant.now());
    }
}