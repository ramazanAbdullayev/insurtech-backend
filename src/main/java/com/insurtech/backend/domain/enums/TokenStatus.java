package com.insurtech.backend.domain.enums;

import lombok.Getter;

@Getter
public enum TokenStatus {
    ACTIVE("Token is valid and can be used once"),
    USED("Token was consumed; issuing a new one was successful"),
    REVOKED("Token (and its family) was explicitly invalidated");

    private final String description;

    TokenStatus(String description) {
        this.description = description;
    }
}
