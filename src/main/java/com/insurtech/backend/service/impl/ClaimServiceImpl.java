package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.domain.enums.ClaimStatus;
import com.insurtech.backend.dto.api.request.ClaimRequest;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import com.insurtech.backend.event.ClaimCreatedEvent;
import com.insurtech.backend.exception.NotFoundException;
import com.insurtech.backend.exception.handler.ErrorCode;
import com.insurtech.backend.mapper.ClaimMapper;
import com.insurtech.backend.processor.ClaimFileProcessor;
import com.insurtech.backend.repository.ClaimRepository;
import com.insurtech.backend.repository.UserRepository;
import com.insurtech.backend.service.ClaimService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

  private final ClaimRepository claimRepository;
  private final UserRepository userRepository;
  private final ClaimMapper claimMapper;
  private final ClaimFileProcessor claimFileProcessor;
  private final ApplicationEventPublisher publisher;

  public List<ClaimResponse> getAll(UUID userId) {
    return claimMapper.toResponseList(claimRepository.findAllByUserId(userId).orElse(List.of()));
  }

  public ClaimResponse getByClaimNumber(String claimNumber) {
    return claimMapper.toResponse(
        claimRepository
            .findByClaimNumber((claimNumber))
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "Claim not found. claimNumber" + claimNumber)));
  }

  public ClaimResponse getById(UUID claimId) {
    return claimMapper.toResponse(
        claimRepository
            .findById((claimId))
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "Claim not found. claimId" + claimId)));
  }

  @Transactional
  public ClaimResponse create(UUID userId, ClaimRequest data, List<MultipartFile> files) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new NotFoundException(ErrorCode.NOT_FOUND, "User not found. id: {}" + userId));

    Claim claim = claimMapper.toEntity(data);
    claim.setUser(user);
    claim.setStatus(ClaimStatus.SUBMITTED);

    Claim savedClaim = claimRepository.save(claim);

    claimFileProcessor.upload(claim, files);

    publisher.publishEvent(new ClaimCreatedEvent(savedClaim.getId()));

    return claimMapper.toResponse(claim);
  }

  @Transactional
  public void delete(String claimNumber) {
    Claim claim =
        claimRepository
            .findByClaimNumber(claimNumber)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        ErrorCode.NOT_FOUND, "Claim not found. claimNumber" + claimNumber));
    claimFileProcessor.delete(claim);
    // ON DELETE CASCADE for both ClaimEstimation and ClaimFile
    claimRepository.deleteByIdBulk(claim.getId());
    log.warn(
        "Claim deleted from db, both ClaimFile, ClaimEstimation also deleted from DB, because ON DELETE CASCADE.");
  }
}
