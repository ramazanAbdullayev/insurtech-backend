package com.insurtech.backend.dto.api.request;

import com.insurtech.backend.validator.XssSafe;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload required to create a new user account")
public record RegisterRequest(
    @Schema(description = "User's first name. Max 255 characters.", example = "John")
        @XssSafe
        @NotBlank
        @Size(max = 255, message = "Max 255 character(s)")
        String firstName,
    @Schema(description = "User's last name. Max 255 characters.", example = "Doe")
        @XssSafe
        @NotBlank
        @Size(max = 255, message = "Max 255 character(s)")
        String lastName,
    @Schema(
            description = "User's email address. Must be unique. Max 320 characters.",
            example = "john.doe@example.com")
        @NotBlank
        @Email(message = "Must be a valid email address")
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
