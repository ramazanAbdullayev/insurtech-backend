package com.insurtech.backend.dto.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank
    @Size(max = 255, message = "Max 255 character(s)")
    String firstName,

    @NotBlank
    @Size(max = 255, message = "Max 255 character(s)")
    String lastName,

    @Email(message = "Must be a valid email address")
    @Size(max = 320, message = "Max 320 character(s)")
    String email,

    @NotBlank
    @Size(min = 8, message = "Min 8 character(s)")
    @Size(max = 128, message = "Max 128 character(s)")
    String password
) {}