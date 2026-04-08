package com.insurtech.backend.service;

import com.insurtech.backend.dto.api.request.LoginRequest;
import com.insurtech.backend.dto.api.request.RefreshTokenRequest;
import com.insurtech.backend.dto.api.request.RegisterRequest;
import com.insurtech.backend.dto.api.response.TokenResponse;
import java.util.UUID;

public interface AuthService {

  void register(RegisterRequest request);

  TokenResponse login(LoginRequest request, String userAgent, String ip);

  TokenResponse refresh(RefreshTokenRequest request, String userAgent, String ip);

  void revokeToken(String rawRefreshToken);

  void logoutAll(UUID userId);
}
