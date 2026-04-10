package com.insurtech.backend.dto.api.response;

import com.insurtech.backend.domain.enums.ClaimEstimationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
@Schema(description = "AI-generated cost estimation result for an insurance claim")
public record ClaimEstimationResponse(
    @Schema(
            description =
                "Confidence score of the AI estimation, expressed as a value between 0.0 and 1.0."
                    + " Higher values indicate greater model certainty.",
            example = "0.87")
        Double aiConfidence,
    @Schema(
            description = "Estimated repair or compensation cost in the policy currency",
            example = "3450.00")
        BigDecimal estimatedCost,
    @Schema(
            description = "Raw text response returned by the AI model prior to parsing",
            example = "Based on the provided images, the estimated repair cost is $3,450.")
        String rawResponse,
    @Schema(
            description =
                "Current status of the estimation process."
                    + " Possible values: ESTIMATING, ESTIMATED, ESTIMATION_FAILED",
            example = "ESTIMATED")
        ClaimEstimationStatus status) {}
