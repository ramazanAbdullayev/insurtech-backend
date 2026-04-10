package com.insurtech.backend.dto.api.response;

import com.insurtech.backend.domain.enums.ClaimFileStatus;
import com.insurtech.backend.domain.enums.ClaimFileType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Metadata for a file attached to an insurance claim")
public record ClaimFileResponse(
    @Schema(description = "File category. Possible values: DOCUMENT, PHOTO", example = "PHOTO")
        ClaimFileType type,
    @Schema(
            description = "Unique S3 object key used to reference or retrieve the file",
            example = "claims/CLM-2024-000123/photo_001.jpg")
        String fileKey,
    @Schema(
            description = "Original filename as supplied by the client",
            example = "damage_front.jpg")
        String originalFilename,
    @Schema(description = "File size in bytes", example = "2048576") Long size,
    @Schema(description = "MIME type of the file", example = "image/jpeg") String contentType,
    @Schema(
            description = "Upload processing status. Possible values: UPLOADING, UPLOADED, FAILED",
            example = "UPLOADED")
        ClaimFileStatus status,
    @Schema(
            description = "ISO-8601 UTC timestamp of when the file was uploaded",
            example = "2024-03-15T14:36:05Z")
        Instant uploadedAt) {}
