package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.domain.enums.UserRole;
import com.insurtech.backend.domain.enums.UserStatus;
import com.insurtech.backend.dto.api.request.LoginRequest;
import com.insurtech.backend.dto.api.request.RefreshTokenRequest;
import com.insurtech.backend.dto.api.request.RegisterRequest;
import com.insurtech.backend.dto.api.response.TokenResponse;
import com.insurtech.backend.exception.AuthException;
import com.insurtech.backend.exception.ErrorCode;
import com.insurtech.backend.repository.UserRepository;
import com.insurtech.backend.security.CustomUserDetails;
import com.insurtech.backend.service.AuthService;
import com.insurtech.backend.service.JwtService;
import com.insurtech.backend.service.RefreshTokenService;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final RefreshTokenService refreshTokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;

  @Value("${auth.jwt.access-token-ttl-seconds}")
  private long accessTokenTtlSeconds;

  @Transactional
  public void register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      log.info("User already exists. email: {}", request.email()); // Email must be masked!!!
      // Intentionally vague to avoid user enumeration
      throw new AuthException(
          ErrorCode.REGISTRATION_FAILED, "Registration could not be completed. Please try again.");
    }

    User user =
        User.builder()
            .firstName(request.firstName())
            .lastName(request.lastName())
            .email(request.email().toLowerCase().strip())
            .passwordHash(passwordEncoder.encode(request.password()))
            .roles(Set.of(UserRole.USER))
            .status(UserStatus.ACTIVE)
            .build();

    userRepository.save(user);
    log.info("New user registered. userId: {}", user.getId());
  }

  @Transactional
  public TokenResponse login(LoginRequest request, String userAgent, String ip) {
    Authentication authentication;
    try {
      authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.email().toLowerCase().strip(), request.password()));
    } catch (AuthenticationException ex) {
      throw new AuthException(ErrorCode.INVALID_CREDENTIALS);
    }

    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

    if (Objects.isNull(principal)) throw new AuthException(ErrorCode.INVALID_CREDENTIALS);

    User user =
        userRepository
            .findById(principal.id())
            .orElseThrow(() -> new AuthException(ErrorCode.INVALID_CREDENTIALS));
    user.setLastLoginAt(Instant.now());
    userRepository.save(user);

    return TokenResponse.withRefresh(
        jwtService.generateAccessToken(user),
        "Bearer",
        accessTokenTtlSeconds,
        refreshTokenService.issue(user, null, userAgent, ip));
  }

  @Transactional
  public TokenResponse refresh(RefreshTokenRequest request, String userAgent, String ip) {
    RefreshTokenServiceImpl.RotationResult rotation =
        refreshTokenService.rotate(request.refreshToken(), userAgent, ip);

    String accessToken = jwtService.generateAccessToken(rotation.user());

    return TokenResponse.withRefresh(
        accessToken, "Bearer", accessTokenTtlSeconds, rotation.newRawToken());
  }

  @Transactional
  public void revokeToken(String rawRefreshToken) {
    refreshTokenService.revokeToken(rawRefreshToken);
  }

  @Transactional
  public void logoutAll(UUID userId) {
    int count = refreshTokenService.revokeAllForUser(userId);
    log.info("User {} logged out from {} sessions", userId, count);
  }
}
