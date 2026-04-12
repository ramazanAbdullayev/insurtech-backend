package com.insurtech.backend.processor;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimFile;
import com.insurtech.backend.exception.PartialDeletionException;
import com.insurtech.backend.service.ClaimFileService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaimFileProcessor {

  private final ClaimFileService claimFileService;

  public void upload(Claim claim, List<MultipartFile> files) {
    for (MultipartFile file : files) {
      try {
        claimFileService.upload(claim, file);
      } catch (Exception e) {
        log.error(
            "Failed to upload file. fileName: {} | claimNumber: {}",
            file.getOriginalFilename(),
            claim.getClaimNumber(),
            e);
      }
    }
  }

  public void delete(Claim claim) {
    List<ClaimFile> claimFiles = claimFileService.getByClaim(claim);
    List<UUID> failed = new ArrayList<>();

    for (ClaimFile file : claimFiles) {
      boolean success = true;
      try {
        success = claimFileService.deleteFromStorage(file.getId());
      } catch (Exception ex) {
        log.error("DELETE_ERROR: {}", ex.getMessage(), ex);
      }

      if (!success) {
        failed.add(file.getId());
      }
    }

    if (!failed.isEmpty()) {
      throw new PartialDeletionException(
          failed, "Files not fully deleted. claimId: " + claim.getId());
    }
  }
}
