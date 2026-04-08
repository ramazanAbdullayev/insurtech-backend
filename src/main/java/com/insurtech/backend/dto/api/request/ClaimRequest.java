package com.insurtech.backend.dto.api.request;

import com.insurtech.backend.validator.XssSafe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record ClaimRequest(
    @NotBlank @XssSafe String accidentType,
    @NotNull Instant occurredAt,
    @NotBlank @XssSafe String location,
    @Size(max = 500) @XssSafe String description,
    boolean isOtherPartyInvolved) {}
