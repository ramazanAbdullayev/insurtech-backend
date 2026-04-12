package com.insurtech.backend.domain.entity;

import com.insurtech.backend.domain.enums.ClaimStatus;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "claim",
    indexes = {
      @Index(name = "claim_user_id_idx", columnList = "user_id"),
      @Index(name = "claim_claim_number_idx", columnList = "claim_number")
    })
public class Claim {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "claim_number", nullable = false, updatable = false, unique = true)
  private String claimNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private User user;

  @Column(name = "accident_type", nullable = false, updatable = false)
  private String accidentType;

  @Column(name = "occurred_at", updatable = false)
  private Instant occurredAt;

  @Column(name = "other_party_involved", nullable = false)
  private boolean isOtherPartyInvolved;

  @Column(name = "location")
  private String location;

  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private ClaimStatus status;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @PrePersist
  void onCreate() {
    this.createdAt = Instant.now();
    this.claimNumber = generateRandomClaimNumber();
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = Instant.now();
  }

  private String generateRandomClaimNumber() {
    int year = LocalDate.now().getYear();
    long random = ThreadLocalRandom.current().nextLong(100_000, 1_000_000);
    return String.format("CLM-%d-%06d", year, random);
  }
}
