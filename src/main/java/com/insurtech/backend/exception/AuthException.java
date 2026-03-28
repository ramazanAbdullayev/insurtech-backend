package com.insurtech.backend.exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        super(errorCode.getDescription());
    }

    public AuthException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        super(message);
    }
}
