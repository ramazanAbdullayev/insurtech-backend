package com.insurtech.backend.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_CREDENTIALS("Invalid email or password"),
    TOKEN_EXPIRED("Token has expired"),
    TOKEN_INVALID("Token is invalid"),
    TOKEN_REVOKED("Token has been revoked"),
    TOKEN_REUSE_DETECTED("Refresh token reuse detected"),
    ACCOUNT_DISABLED("Account is disabled"),
    FORBIDDEN("Access is forbidden"),
    REGISTRATION_FAILED("Registration failed"),
    VALIDATION_ERROR("Validation error"),
    NOT_FOUND("Not found"),
    INTERNAL_ERROR("Internal server error");

    private final String description;

    ErrorCode(String description) {
        this.description = description;
    }
}