package com.insurtech.backend.service.impl;

import com.insurtech.backend.client.AIAnalysisClient;
import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimEstimation;
import com.insurtech.backend.domain.enums.ClaimFileStatus;
import com.insurtech.backend.domain.enums.ClaimFileType;
import com.insurtech.backend.domain.enums.ClaimStatus;
import com.insurtech.backend.dto.ai.request.AIAnalysisRequest;
import com.insurtech.backend.dto.ai.response.AIAnalysisResponse;
import com.insurtech.backend.dto.api.response.ClaimFileResponse;
import com.insurtech.backend.exception.AIServiceException;
import com.insurtech.backend.exception.handler.ErrorCode;
import com.insurtech.backend.repository.ClaimRepository;
import com.insurtech.backend.service.ClaimEstimationService;
import com.insurtech.backend.service.ClaimFileService;
import com.insurtech.backend.service.StorageService;
import com.insurtech.backend.service.tx.ClaimEstimationTxService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimEstimationServiceImpl implements ClaimEstimationService {

  private final ClaimEstimationTxService txService;
  private final ClaimRepository claimRepository;
  private final ClaimFileService claimFileService;
  private final StorageService storageService;
  private final AIAnalysisClient aIAnalysisClient;

  @Value("${spring.cloud.aws.s3.presigned-url.ttl-minutes:15}")
  private int presignedUrlTtlMinutes;

  @Override
  public void estimate(UUID claimId) {
    Claim claim = claimRepository.findById(claimId).orElse(null);

    if (claim == null) {
      log.warn("ESTIMATION_SKIPPED | claim not found | {}", claimId);
      return;
    }

    if (claim.getStatus() != ClaimStatus.SUBMITTED) {
      log.warn("ESTIMATION_SKIPPED | invalid status | {} | {}", claimId, claim.getStatus());
      return;
    }

    ClaimEstimation job = txService.getOrCreate(claim);
    boolean shouldCallAI;

    try {
      shouldCallAI = txService.markProcessing(job.getId());
    } catch (Exception ex) {
      log.error("ESTIMATION_FAILED | claimId: {}", claimId, ex);
      txService.saveFailure(job.getId(), ex.getMessage());
      throw ex;
    }

    log.warn("CALL_AI | shouldCallAi | {} ", shouldCallAI);
    if (shouldCallAI) callAI(job.getId(), claimId);
  }

  public void delete(Claim claim) {
    txService.delete(claim);
  }

  private void callAI(UUID estimationId, UUID claimId) {
    List<String> imageUrls = resolveImageUrls(claimId);

    if (imageUrls.isEmpty()) {
      txService.saveFailure(estimationId, "No images found");
      return;
    }

    try {
      log.info("AI_CALL_STARTED | claimId: {}", claimId);
      AIAnalysisResponse response = aIAnalysisClient.analyze(new AIAnalysisRequest(imageUrls));

      if (!Objects.isNull(response)) {
        txService.saveSuccess(estimationId, response);
      } else {
        txService.saveFailure(estimationId, "AIAnalysisResponse is null");
      }
    } catch (RestClientResponseException ex) {
      txService.saveFailure(
          estimationId, "HTTP " + ex.getStatusCode() + " | " + ex.getResponseBodyAsString());
      throw new AIServiceException(
          ErrorCode.AI_SERVICE_ERROR, "AI service error: " + ex.getMessage());
    } catch (ResourceAccessException ex) {
      txService.saveFailure(estimationId, "Timeout / unreachable");
      throw new AIServiceException(
          ErrorCode.AI_SERVICE_ERROR, "AI service unavailable: " + ex.getMessage());
    } catch (Exception ex) {
      txService.saveFailure(estimationId, ex.getMessage());
      throw new AIServiceException(
          ErrorCode.AI_SERVICE_ERROR, "Something went wrong when AI call: " + ex.getMessage());
    }
  }

  private List<String> resolveImageUrls(UUID claimId) {
    log.debug(
        "Resolving presigned URLs | claimId: {} | ttlMinutes: {}", claimId, presignedUrlTtlMinutes);

    return claimFileService.getByClaimId(claimId).stream()
        .filter(f -> f.type() == ClaimFileType.PHOTO && f.status() == ClaimFileStatus.UPLOADED)
        .map(ClaimFileResponse::fileKey)
        .map(storageService::getPresignedUrl)
        .toList();
  }
}
