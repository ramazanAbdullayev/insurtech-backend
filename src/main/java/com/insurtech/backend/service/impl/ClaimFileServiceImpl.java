package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimFile;
import com.insurtech.backend.domain.enums.ClaimFileStatus;
import com.insurtech.backend.domain.enums.ClaimFileType;
import com.insurtech.backend.dto.api.response.ClaimFileResponse;
import com.insurtech.backend.exception.ErrorCode;
import com.insurtech.backend.exception.InvalidValueException;
import com.insurtech.backend.repository.ClaimFileRepository;
import com.insurtech.backend.service.ClaimFileService;
import com.insurtech.backend.service.StorageService;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimFileServiceImpl implements ClaimFileService {

  private final ClaimFileRepository claimFileRepository;
  private final StorageService storageService;

  public List<ClaimFileResponse> getByClaimId(UUID claimId) {
    return claimFileRepository.findAllByClaimId(claimId);
  }

  @Transactional
  public void upload(Claim claim, List<MultipartFile> files) {
    if (Objects.isNull(claim) || Objects.isNull(files))
      throw new InvalidValueException(ErrorCode.INVALID_VALUE, "Claim or files is null");

    for (MultipartFile file : files) {
      log.info(
          "START: uploading file to storage service (S3). FILE_NAME: {}",
          file.getOriginalFilename());
      String fileKey = storageService.upload(claim.getClaimNumber(), file);
      log.info(
          "END: uploaded file to storage service (S3). FILE_NAME: {} | FILE_KEY: {}",
          file.getOriginalFilename(),
          fileKey);

      ClaimFile claimFile =
          ClaimFile.builder()
              .claim(claim)
              .uploadedAt(Instant.now())
              .originalFilename(file.getOriginalFilename())
              .contentType(file.getContentType())
              .size(file.getSize())
              .type(
                  ClaimFileType.PHOTO) // need to add condition to identify it is PHOTO ot DOCUMENT
              .fileKey(fileKey)
              .status(ClaimFileStatus.UPLOADED)
              .build();

      ClaimFile savedClaimFile = claimFileRepository.save(claimFile);
      log.info("Claim file have been saved to the DB. claimFileId: {}", savedClaimFile.getId());
    }
  }

  @Transactional
  public void delete(Claim claim) {
    List<ClaimFile> claimFiles = claimFileRepository.findAllByClaim(claim);

    claimFileRepository.deleteAll(claimFiles);
    log.info("Claim files data have been deleted from the DB. claimNumber: {}", claim.getClaimNumber());
    claimFiles.forEach(file -> storageService.delete(file.getFileKey()));
    log.info("Claim files have been deleted from storage service (S3). claimNumber: {}", claim.getClaimNumber());
  }
}
