package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import com.insurtech.backend.dto.api.request.LoginRequest;
import com.insurtech.backend.dto.api.request.RefreshTokenRequest;
import com.insurtech.backend.dto.api.request.RegisterRequest;
import com.insurtech.backend.dto.api.response.TokenResponse;
import com.insurtech.backend.service.AuthService;
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

/*
 * TODO: Implement revoking all old refreshTokens when I login and get new tokens (refresh and access)
 *
 * */

@RestController
@RequestMapping(AuthController.URL)
@RequiredArgsConstructor
public class AuthController {

  public static final String URL = ApiConstants.BASE_URL + "/auth";

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
    authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(
      @Valid @RequestBody LoginRequest request, HttpServletRequest httpReq) {
    return ResponseEntity.ok(
        authService.login(request, httpReq.getHeader("User-Agent"), httpReq.getRemoteAddr()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenResponse> refresh(
      @Valid @RequestBody RefreshTokenRequest request, HttpServletRequest httpReq) {
    return ResponseEntity.ok(
        authService.refresh(request, httpReq.getHeader("User-Agent"), httpReq.getRemoteAddr()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
    authService.revokeToken(request.refreshToken());
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/logout-all")
  public ResponseEntity<Void> logoutAll(@AuthenticationPrincipal Jwt jwt) {
    authService.logoutAll(UUID.fromString(jwt.getSubject()));
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
