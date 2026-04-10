package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import com.insurtech.backend.dto.api.request.ClaimRequest;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import com.insurtech.backend.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(
    name = "Claims",
    description =
        "Manage insurance claims submitted by authenticated users. Supports claim creation"
            + " with file attachments, retrieval by claim number or by the authenticated"
            + " user, and deletion. All endpoints require a valid JWT access token.")
@RestController
@RequestMapping(ClaimController.URL)
@RequiredArgsConstructor
public class ClaimController {

  public static final String URL = ApiConstants.BASE_URL + "/claims";

  private final ClaimService claimService;

  @Operation(
      summary = "Get claim by claim number",
      description =
          "Retrieves the full details of a single claim identified by its unique claim"
              + " number. Returns 404 if no claim with the given number exists.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Claim found — returns claim details",
        content = @Content(schema = @Schema(implementation = ClaimResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "claimNumber query parameter is missing",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid JWT access token",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "404",
        description = "Claim not found",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping
  public ResponseEntity<Object> getByClaimNumber(
      @Parameter(description = "Unique claim number", example = "CLM-2024-000123", required = true)
          @RequestParam
          String claimNumber) {
    return ResponseEntity.ok(claimService.getByClaimNumber(claimNumber));
  }

  @Operation(
      summary = "List all claims for the authenticated user",
      description =
          "Returns all claims submitted by the currently authenticated user, identified"
              + " via the JWT subject. Returns an empty list if the user has no claims.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Returns the list of claims (may be empty)",
        content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = ClaimResponse.class)))),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid JWT access token",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @SecurityRequirement(name = "bearerAuth")
  @GetMapping("/all")
  public ResponseEntity<List<ClaimResponse>> getAll(@AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.ok(claimService.getAll(UUID.fromString(jwt.getSubject())));
  }

  @Operation(
      summary = "Submit a new claim",
      description =
          "Creates a new insurance claim for the authenticated user. Accepts a multipart"
              + " request containing structured claim data and one or more supporting files"
              + " (photos or documents). Returns the created claim on success.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Claim created successfully — returns the new claim",
        content = @Content(schema = @Schema(implementation = ClaimResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed — missing or malformed claim data",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid JWT access token",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description =
          "Multipart request: 'data' part contains the claim JSON payload; 'files' part"
              + " contains one or more attachment files",
      required = true,
      content = @Content(schema = @Schema(implementation = ClaimRequest.class)))
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/create")
  public ResponseEntity<ClaimResponse> create(
      @Valid @RequestPart("data") ClaimRequest data,
      @RequestPart("files") List<MultipartFile> files,
      @AuthenticationPrincipal Jwt jwt) {
    return ResponseEntity.ok(claimService.create(UUID.fromString(jwt.getSubject()), data, files));
  }

  @Operation(
      summary = "Delete a claim by claim number",
      description =
          "Permanently deletes the claim identified by the given claim number. Returns 204"
              + " on success with no response body. Returns 404 if the claim does not exist.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Claim deleted successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "claimNumber query parameter is missing",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "401",
        description = "Missing or invalid JWT access token",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "404",
        description = "Claim not found",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @SecurityRequirement(name = "bearerAuth")
  @DeleteMapping("/delete")
  public ResponseEntity<Void> deleteByClaimNumber(
      @Parameter(description = "Unique claim number", example = "CLM-2024-000123", required = true)
          @RequestParam
          String claimNumber) {
    claimService.delete(claimNumber);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
