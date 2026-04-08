package com.insurtech.backend.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("expires_in (second)") long expiresIn,
    @JsonProperty("refresh_token") String refreshToken) {
  public static TokenResponse withRefresh(
      String accessToken, String tokenType, long ttl, String refreshToken) {
    return new TokenResponse(accessToken, tokenType, ttl, refreshToken);
  }

  public static TokenResponse accessOnly(String accessToken, String tokenType, long ttl) {
    return new TokenResponse(accessToken, tokenType, ttl, null);
  }
}
