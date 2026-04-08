package com.insurtech.backend.dto.api.request;

import com.insurtech.backend.validator.XssSafe;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @XssSafe @NotBlank @Size(max = 255, message = "Max 255 character(s)") String firstName,
    @XssSafe @NotBlank @Size(max = 255, message = "Max 255 character(s)") String lastName,
    @NotBlank
        @Email(message = "Must be a valid email address")
        @Size(max = 320, message = "Max 320 character(s)")
        String email,
    @NotBlank
        @Size(min = 8, message = "Min 8 character(s)")
        @Size(max = 128, message = "Max 128 character(s)")
        String password) {}
