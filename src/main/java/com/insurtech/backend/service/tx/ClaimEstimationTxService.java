package com.insurtech.backend.service.tx;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimEstimation;
import com.insurtech.backend.domain.enums.ClaimEstimationStatus;
import com.insurtech.backend.dto.ai.response.AIAnalysisResponse;
import com.insurtech.backend.exception.AIServiceException;
import com.insurtech.backend.exception.handler.ErrorCode;
import com.insurtech.backend.repository.ClaimEstimationRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimEstimationTxService {
  private final ClaimEstimationRepository claimEstimationRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public ClaimEstimation getOrCreate(Claim claim) {
    claimEstimationRepository.insertIfAbsent((claim.getId()));

    return claimEstimationRepository
        .findByClaim_Id(claim.getId())
        .orElseThrow(
            () ->
                new AIServiceException(
                    ErrorCode.AI_SERVICE_ERROR,
                    "Estimation job missing after upsert | claimId: " + claim.getId()));
  }

  @Transactional
  public boolean markProcessing(UUID estimationId) {
    ClaimEstimation job =
        claimEstimationRepository
            .lockById(estimationId)
            .orElseThrow(
                () ->
                    new AIServiceException(
                        ErrorCode.AI_SERVICE_ERROR,
                        "ClaimEstimation not found. id: " + estimationId));

    if (job.getStatus() == ClaimEstimationStatus.ESTIMATED
        || job.getStatus() == ClaimEstimationStatus.ESTIMATING) {
      return false;
    }

    job.setStatus(ClaimEstimationStatus.ESTIMATING);
    job.setAttemptCount(job.getAttemptCount() + 1);
    claimEstimationRepository.save(job);
    log.info("ESTIMATION_PROCESSING | estimationStatus: {} |", job.getStatus());
    return true;
  }

  @Transactional
  public void saveSuccess(UUID estimationId, AIAnalysisResponse response) {
    ClaimEstimation job =
        claimEstimationRepository
            .lockById(estimationId)
            .orElseThrow(
                () ->
                    new AIServiceException(
                        ErrorCode.AI_SERVICE_ERROR,
                        "ClaimEstimation not found. id: " + estimationId));

    if (job.getStatus() == ClaimEstimationStatus.ESTIMATED) {
      return;
    }

    job.setEstimatedCost(response.estimatedRepairCost());
    job.setAiConfidence(response.confidenceScore());
    job.setRawResponse(serialize(response));
    job.setStatus(ClaimEstimationStatus.ESTIMATED);
    claimEstimationRepository.save(job);

    log.info("AI_CALL_SUCCESS | estimationId: {}", estimationId);
  }

  @Transactional
  public void saveFailure(UUID estimationId, String error) {
    ClaimEstimation job =
        claimEstimationRepository
            .lockById(estimationId)
            .orElseThrow(
                () ->
                    new AIServiceException(
                        ErrorCode.AI_SERVICE_ERROR,
                        "ClaimEstimation not found. id: " + estimationId));

    if (job.getStatus() == ClaimEstimationStatus.ESTIMATED) {
      return;
    }

    job.setStatus(ClaimEstimationStatus.ESTIMATION_FAILED);
    job.setRawResponse(error);
    claimEstimationRepository.save(job);

    log.warn("ESTIMATION_MARKED_FAILED | estimationId: {} | error: {}", estimationId, error);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void delete(Claim claim) {
    log.info("DELETING | claimId {} ", claim.getId());
    claimEstimationRepository.deleteByClaim(claim);
    log.info("DELETED | claimId {} ", claim.getId());
  }

  private String serialize(AIAnalysisResponse response) {
    try {
      return objectMapper.writeValueAsString(response);
    } catch (Exception e) {
      log.error("ESTIMATION_SERIALIZATION_FAILED | response: {}", response);
      return "SERIALIZATION_FAILED: " + response.toString();
    }
  }
}
