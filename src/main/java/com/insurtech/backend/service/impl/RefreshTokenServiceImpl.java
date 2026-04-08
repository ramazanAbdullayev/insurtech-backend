package com.insurtech.backend.service.impl;

import com.insurtech.backend.Utils.HashUtil;
import com.insurtech.backend.domain.entity.RefreshToken;
import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.domain.enums.RefreshTokenStatus;
import com.insurtech.backend.exception.AuthException;
import com.insurtech.backend.exception.ErrorCode;
import com.insurtech.backend.repository.RefreshTokenRepository;
import com.insurtech.backend.service.RefreshTokenService;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  @Getter
  @Value("${auth.jwt.refresh-token-ttl-days}")
  private long refreshTokenTtlDays;

  /**
   * Issues a new refresh token.
   *
   * @param user the owner
   * @param familyId pass an existing familyId to continue a rotation chain, or pass null to start a
   *     fresh chain (new login)
   * @param userAgent optional device metadata
   * @param ip optional IP for audit
   * @return the raw (unhashed) token string — send to client, never persist this
   */
  @Transactional
  public String issue(User user, UUID familyId, String userAgent, String ip) {
    String rawToken =
        UUID.randomUUID().toString().replace("-", "")
            + UUID.randomUUID().toString().replace("-", "");
    familyId = (familyId != null) ? familyId : UUID.randomUUID();
    Instant now = Instant.now();

    RefreshToken refreshToken =
        RefreshToken.builder()
            .tokenHash(HashUtil.sha256Hex(rawToken))
            .user(user)
            .familyId(familyId)
            .status(RefreshTokenStatus.ACTIVE)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(refreshTokenTtlDays * 86_400L))
            .userAgent(userAgent)
            .ipAddress(ip)
            .build();

    refreshTokenRepository.save(refreshToken);
    log.info("Refresh token was issued successfully. familyId: {}", familyId);
    return rawToken;
  }

  /**
   * Core of Refresh Token Rotation with reuse detection.
   *
   * <p>1. Hash the incoming raw token and look it up. 2. If REVOKED or not found → reject (possible
   * replay after family kill). 3. If USED → reuse detected → revoke the entire family → reject. 4.
   * If ACTIVE and not expired → mark as USED → issue new token in same family.
   */
  @Transactional
  public RotationResult rotate(String rawToken, String userAgent, String ip) {
    RefreshToken current =
        refreshTokenRepository
            .findByTokenHash(HashUtil.sha256Hex(rawToken))
            .orElseThrow(
                () -> new AuthException(ErrorCode.TOKEN_INVALID, "Refresh token not found."));

    if (RefreshTokenStatus.USED.equals(current.getStatus())) {
      log.warn(
          "SECURITY: Refresh token reuse detected — revoking entire family {} | UserId: {}",
          current.getFamilyId(),
          current.getUser().getId());
      refreshTokenRepository.revokeFamily(current.getFamilyId());
      throw new AuthException(
          ErrorCode.TOKEN_REUSE_DETECTED,
          "Refresh token reuse detected. All sessions invalidated. Please log in again.");
    }

    if (RefreshTokenStatus.REVOKED.equals(current.getStatus())) {
      log.info(
          "Refresh token has been revoked — familyId: {} | UserId: {}",
          current.getFamilyId(),
          current.getUser().getId());
      throw new AuthException(ErrorCode.TOKEN_REVOKED, "Refresh token has been revoked");
    }

    if (current.getExpiresAt().isBefore(Instant.now())) {
      log.info(
          "Refresh token has been expired — familyId: {} | UserId: {}",
          current.getFamilyId(),
          current.getUser().getId());
      current.setStatus(RefreshTokenStatus.REVOKED);
      refreshTokenRepository.save(current);
      throw new AuthException(ErrorCode.TOKEN_EXPIRED, "Refresh token has been expired");
    }

    // Mark the current token as USED (not deleted — kept for audit/reuse detection)
    current.setStatus(RefreshTokenStatus.USED);
    current.setUsedAt(Instant.now());
    refreshTokenRepository.save(current);

    // Issue a new token in the same family
    String newRawToken = issue(current.getUser(), current.getFamilyId(), userAgent, ip);

    return new RotationResult(current.getUser(), newRawToken);
  }

  @Transactional
  public void revokeToken(String rawToken) {
    refreshTokenRepository
        .findByTokenHash(HashUtil.sha256Hex(rawToken))
        .ifPresent(
            t -> {
              t.setStatus(RefreshTokenStatus.REVOKED);
              refreshTokenRepository.save(t);
            });
  }

  @Transactional
  public int revokeAllForUser(UUID userId) {
    return refreshTokenRepository.revokeAllActiveForUser(userId);
  }

  public record RotationResult(User user, String newRawToken) {}
}
