package com.insurtech.backend.dto.api.response;

import com.insurtech.backend.domain.enums.ClaimStatus;
import java.time.Instant;

public record ClaimResponse(
    String claimNumber,
    String accidentType,
    String location,
    String description,
    ClaimStatus status,
    Instant createdAt) {}
