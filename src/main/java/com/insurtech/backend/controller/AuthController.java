package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import com.insurtech.backend.dto.api.request.LoginRequest;
import com.insurtech.backend.dto.api.request.RefreshTokenRequest;
import com.insurtech.backend.dto.api.request.RegisterRequest;
import com.insurtech.backend.dto.api.response.TokenResponse;
import com.insurtech.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Authentication",
    description =
        "Provides user authentication and authorization flows, including registration,"
            + " login, JWT access token issuance, refresh token renewal, and revocation"
            + " of active sessions.")
@RestController
@RequestMapping(AuthController.URL)
@RequiredArgsConstructor
public class AuthController {

  public static final String URL = ApiConstants.BASE_URL + "/auth";

  private final AuthService authService;

  @Operation(
      summary = "Register a new user",
      description =
          "Creates a new user account with the provided personal details and credentials."
              + " Returns 201 on success with no response body. The account is immediately"
              + " active and can be used to log in.")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Account created successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed — missing or malformed fields",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "409",
        description = "Email address is already registered",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "User registration details including name, email, and password",
      required = true,
      content = @Content(schema = @Schema(implementation = RegisterRequest.class)))
  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(
      summary = "Authenticate user",
      description =
          "Validates credentials and issues a short-lived JWT access token alongside a"
              + " refresh token. User-Agent and IP address are recorded with the session."
              + " Use the refresh token at /auth/refresh to obtain a new access token"
              + " without re-authenticating.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Authentication successful — returns access and refresh tokens",
        content = @Content(schema = @Schema(implementation = TokenResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed — missing or malformed fields",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "401",
        description = "Invalid email or password",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "User credentials",
      required = true,
      content = @Content(schema = @Schema(implementation = LoginRequest.class)))
  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(
      @Valid @RequestBody LoginRequest request, HttpServletRequest httpReq) {
    return ResponseEntity.ok(
        authService.login(request, httpReq.getHeader("User-Agent"), httpReq.getRemoteAddr()));
  }

  @Operation(
      summary = "Refresh access token",
      description =
          "Exchanges a valid, non-expired refresh token for a new JWT access token and a"
              + " rotated refresh token. The submitted refresh token is revoked immediately"
              + " after use. User-Agent and IP address are recorded with the new session.")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Token refreshed — returns new access and refresh tokens",
        content = @Content(schema = @Schema(implementation = TokenResponse.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed — refresh_token field is blank",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "401",
        description = "Refresh token is invalid, expired, or already revoked",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "The refresh token to exchange",
      required = true,
      content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class)))
  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(
      @Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpReq) {
    return ResponseEntity.ok(
        authService.refresh(request, httpReq.getHeader("User-Agent"), httpReq.getRemoteAddr()));
  }

  @Operation(
      summary = "Revoke a refresh token",
      description =
          "Revokes the supplied refresh token, terminating the session it belongs to."
              + " The associated JWT access token remains valid until its natural expiry.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Refresh token revoked successfully"),
    @ApiResponse(
        responseCode = "400",
        description = "Validation failed — refresh_token field is blank",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "401",
        description = "Refresh token is invalid or already revoked",
        content = @Content(schema = @Schema())),
    @ApiResponse(
        responseCode = "500",
        description = "Unexpected server error",
        content = @Content(schema = @Schema()))
  })
  @io.swagger.v3.oas.annotations.parameters.RequestBody(
      description = "The refresh token to revoke",
      required = true,
      content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class)))
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
    authService.revokeToken(request.refreshToken());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @Operation(
      summary = "Revoke all refresh tokens",
      description =
          "Revokes every active refresh token for the authenticated user, terminating all"
              + " sessions across all devices simultaneously. Requires a valid JWT access"
              + " token in the Authorization header.")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "All sessions terminated successfully"),
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
  @PostMapping("/logout-all")
  public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal Jwt jwt) {
    authService.logoutAll(UUID.fromString(jwt.getSubject()));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
