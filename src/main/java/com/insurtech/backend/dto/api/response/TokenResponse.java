package com.insurtech.backend.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
        @JsonProperty("access_token") String AccessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("refresh_token") String refreshToken
) {
    public static TokenResponse withRefresh(String access, String tokenType, long ttl, String refresh) {
        return new TokenResponse(access, tokenType, ttl, refresh);
    }

    public static TokenResponse accessOnly(String access, String tokenType, long ttl) {
        return new TokenResponse(access, tokenType, ttl, null);
    }
}
