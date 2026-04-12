package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimFile;
import com.insurtech.backend.domain.enums.ClaimFileStatus;
import com.insurtech.backend.domain.enums.ClaimFileType;
import com.insurtech.backend.dto.api.response.ClaimFileResponse;
import com.insurtech.backend.repository.ClaimFileRepository;
import com.insurtech.backend.service.ClaimFileService;
import com.insurtech.backend.service.StorageService;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/*
 * FIXME: Add scheduler for retry the next cases: stale UPLOADING | FAILED_UPLOAD | FAILED_DELETE
 *
 * */

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimFileServiceImpl implements ClaimFileService {

  private final ClaimFileRepository claimFileRepository;
  private final StorageService storageService;
  private final Clock clock;

  @Override
  public List<ClaimFileResponse> getByClaimId(UUID claimId) {
    return claimFileRepository.findAllByClaimId(claimId);
  }

  @Override
  public List<ClaimFile> getByClaim(Claim claim) {
    return claimFileRepository.findAllByClaim(claim);
  }

  @Override
  @Transactional
  public void upload(Claim claim, MultipartFile file) {
    ClaimFile claimFile =
        claimFileRepository.save(
            ClaimFile.builder()
                .claim(claim)
                .originalFilename(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .type(resolveFileType(file.getContentType(), file.getOriginalFilename()))
                .fileKey("")
                .status(ClaimFileStatus.UPLOADING)
                .uploadingAt(Instant.now(clock))
                .build());

    claimFileRepository.flush();
    log.info(
        "Claim file record created. claimFileId: {} | fileName: {}",
        claimFile.getId(),
        file.getOriginalFilename());

    try {
      String fileKey = storageService.upload(claim.getClaimNumber(), file);
      claimFile.setFileKey(fileKey);
      claimFile.setUploadedAt(Instant.now(clock));
      claimFile.setStatus(ClaimFileStatus.UPLOADED);
      claimFileRepository.save(claimFile);
    } catch (Exception e) {
      claimFile.setStatus(ClaimFileStatus.FAILED_UPLOAD);
      claimFileRepository.save(claimFile);
      log.error(
          "File upload failed - record marked FAILED_UPLOAD for retry. claimFileId: {} | fileName: {}",
          claimFile.getId(),
          file.getOriginalFilename(),
          e);
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean deleteFromStorage(UUID claimFileId) {
    ClaimFile claimFile = claimFileRepository.findById(claimFileId).orElseThrow();

    if (claimFile.getStatus() == ClaimFileStatus.DELETED) {
      log.info("Claim file already deleted from storage and marked DELETED: {}", claimFile.getId());
      return true;
    }

    if (claimFile.getFileKey() == null || claimFile.getFileKey().isBlank()) {
      claimFile.setStatus(ClaimFileStatus.DELETED);
      claimFileRepository.save(claimFile);
      log.info("Claim file marked DELETED (no storage key). claimFileId: {}", claimFile.getId());
      return true;
    }

    boolean deletedFromStorage;
    try {
      storageService.delete(claimFile.getFileKey());
      deletedFromStorage = true;
    } catch (Exception e) {
      log.error(
          "Storage deletion failed. claimFileId: {} | fileKey: {}",
          claimFile.getId(),
          claimFile.getFileKey(),
          e);
      deletedFromStorage = false;
    }

    if (deletedFromStorage) {
      try {
        claimFile.setStatus(ClaimFileStatus.DELETED);
        claimFileRepository.save(claimFile);
        log.info("Marked as DELETED. claimFileId: {}", claimFile.getId());
      } catch (Exception e) {
        log.error("Failed to mark DELETED. claimFileId: {}", claimFile.getId());
      }
    } else {
      try {
        claimFile.setStatus(ClaimFileStatus.FAILED_DELETE);
        claimFileRepository.save(claimFile);
      } catch (Exception ex) {
        log.error("Failed to mark FAILED_DELETE. claimFileId: {}", claimFile.getId(), ex);
      }
    }

    return true;
  }

  private ClaimFileType resolveFileType(String contentType, String filename) {
    if (contentType == null) {
      log.warn("Content-Type is null, defaulting to UNKNOWN. fileName: {}", filename);
      return ClaimFileType.UNKNOWN;
    }
    if (contentType.startsWith("image/")) {
      return ClaimFileType.PHOTO;
    }
    return ClaimFileType.DOCUMENT;
  }
}
