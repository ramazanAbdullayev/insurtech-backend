package com.insurtech.backend.domain.entity;

import com.insurtech.backend.domain.enums.ClaimEstimationStatus;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "claim_estimation",
        indexes = @Index(name = "claim_estimation_claim_id_idx", columnList = "claim_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimEstimation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "claim_id", nullable = false, updatable = false)
    private Claim claim;

    @Column(name = "ai_confidence")
    private Double aiConfidence;

    @Column(name = "estimated_cost")
    private BigDecimal estimatedCost;

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClaimEstimationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}
