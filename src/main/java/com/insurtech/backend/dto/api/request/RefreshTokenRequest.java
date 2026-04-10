package com.insurtech.backend.dto.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload carrying the refresh token to be exchanged or revoked")
public record RefreshTokenRequest(
    @Schema(
            description =
                "A valid, non-expired refresh token issued by /auth/login or /auth/refresh",
            example = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiI...")
        @NotBlank
        @JsonProperty("refresh_token")
        String refreshToken) {}
