package com.insurtech.backend.dto.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT tokens returned after successful authentication or token refresh")
public record TokenResponse(
    @Schema(
            description =
                "Short-lived JWT access token. Include in Authorization: Bearer <token> header.",
            example = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI...")
        @JsonProperty("access_token")
        String accessToken,
    @Schema(description = "Token type. Always 'Bearer'.", example = "Bearer")
        @JsonProperty("token_type")
        String tokenType,
    @Schema(
            description = "Access token lifetime in seconds from the time of issuance.",
            example = "900")
        @JsonProperty("expires_in (second)")
        long expiresIn,
    @Schema(
            description =
                "Long-lived refresh token used to obtain a new access token at /auth/refresh."
                    + " Null when only an access token was issued.",
            example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4...")
        @JsonProperty("refresh_token")
        String refreshToken) {

  public static TokenResponse withRefresh(
      String accessToken, String tokenType, long ttl, String refreshToken) {
    return new TokenResponse(accessToken, tokenType, ttl, refreshToken);
  }

  public static TokenResponse accessOnly(String accessToken, String tokenType, long ttl) {
    return new TokenResponse(accessToken, tokenType, ttl, null);
  }
}
