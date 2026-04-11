package com.insurtech.backend.dto.api.request;

import com.insurtech.backend.validator.XssSafe;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;

@Schema(description = "Payload required to submit a new insurance claim")
public record ClaimRequest(
    @Schema(
            description = "Category of the accident (e.g., collision, theft, fire, flood)",
            example = "Collision")
        @NotBlank
        @XssSafe
        String accidentType,
    @Schema(
            description = "ISO-8601 UTC timestamp of when the accident occurred",
            example = "2024-03-15T14:30:00Z")
        @NotNull
        Instant occurredAt,
    @Schema(
            description = "Physical location where the accident took place",
            example = "205 Heydar Aliyev Avenue, Baku")
        @NotBlank
        @XssSafe
        String location,
    @Schema(
            description = "Optional narrative description of the incident. Max 500 characters.",
            example =
                "Vehicle was rear-ended at a traffic light. Significant damage to the rear bumper and trunk.",
            maxLength = 500)
        @Size(max = 500)
        @XssSafe
        String description,
    @Schema(
            description =
                "Indicates whether a third party (another driver, pedestrian, etc.) was involved",
            example = "true")
        boolean isOtherPartyInvolved) {}
