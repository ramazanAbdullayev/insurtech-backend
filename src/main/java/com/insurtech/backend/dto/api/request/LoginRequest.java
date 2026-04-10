package com.insurtech.backend.dto.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Credentials used to authenticate a user and obtain JWT tokens")
public record LoginRequest(
    @Schema(
            description = "Registered email address. Max 320 characters.",
            example = "john.doe@example.com")
        @NotBlank
        @Email
        @Size(max = 320, message = "Max 320 character(s)")
        String email,
    @Schema(
            description = "Account password. Min 8 characters, max 128 characters.",
            example = "S3cur3P@ssword",
            minLength = 8,
            maxLength = 128)
        @NotBlank
        @Size(min = 8, message = "Min 8 character(s)")
        @Size(max = 128, message = "Max 128 character(s)")
        String password) {}
