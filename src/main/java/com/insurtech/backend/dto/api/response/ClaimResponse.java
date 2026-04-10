package com.insurtech.backend.dto.api.response;

import com.insurtech.backend.domain.enums.ClaimStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Summarized representation of an insurance claim")
public record ClaimResponse(
    @Schema(description = "System-generated unique claim identifier", example = "CLM-2024-000123")
        String claimNumber,
    @Schema(description = "Category of the accident", example = "Collision") String accidentType,
    @Schema(
            description = "Physical location where the accident occurred",
            example = "123 Main St, Springfield, IL 62701")
        String location,
    @Schema(
            description = "Narrative description of the incident",
            example = "Vehicle was rear-ended at a traffic light.")
        String description,
    @Schema(
            description =
                "Current processing status of the claim."
                    + " Possible values: SUBMITTED, PENDING, UNDER_REVIEW, APPROVED, REPAIRED, PAID",
            example = "SUBMITTED")
        ClaimStatus status,
    @Schema(
            description = "ISO-8601 UTC timestamp of when the claim was submitted",
            example = "2024-03-15T14:35:22Z")
        Instant createdAt) {}
