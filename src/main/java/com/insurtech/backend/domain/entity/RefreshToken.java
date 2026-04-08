package com.insurtech.backend.domain.entity;

import com.insurtech.backend.domain.enums.RefreshTokenStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "refresh_tokens",
    indexes = {
      @Index(name = "rt_token_hash_idx", columnList = "token_hash"),
      @Index(name = "rt_user_id_idx", columnList = "user_id"),
      @Index(name = "rt_family_id_idx", columnList = "family_id")
    })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * SHA-256 hex-encoded hash of the raw token string. The raw token is sent to the client once and
   * never stored in plaintext.
   */
  @Column(name = "token_hash", nullable = false, unique = true, length = 64)
  private String tokenHash;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "family_id", nullable = false)
  private UUID familyId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 10)
  private RefreshTokenStatus status;

  @Column(name = "issued_at", nullable = false, updatable = false)
  private Instant issuedAt;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "used_at")
  private Instant usedAt;

  // Optional: device/session metadata
  @Column(name = "user_agent", length = 512)
  private String userAgent;

  @Column(name = "ip_address", length = 45)
  private String ipAddress;
}
