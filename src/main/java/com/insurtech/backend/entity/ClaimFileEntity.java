package com.insurtech.backend.entity;

import com.insurtech.backend.constants.enums.api.FileStatus;
import com.insurtech.backend.constants.enums.api.FileType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "claim_file")
public class ClaimFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID claimId;

    private FileType type;

    private String contentType;

    private FileStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;
}
