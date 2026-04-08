package com.insurtech.backend.domain.enums;

import lombok.Getter;

@Getter
public enum RefreshTokenStatus {
  ACTIVE("Refresh token is valid and can be used once"),
  USED("Refresh token was consumed; issuing a new one was successful"),
  REVOKED("Refresh token (and its family) was explicitly invalidated");

  private final String description;

  RefreshTokenStatus(String description) {
    this.description = description;
  }
}
