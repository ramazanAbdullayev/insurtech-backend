package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.domain.enums.ClaimStatus;
import com.insurtech.backend.dto.api.request.ClaimRequest;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import com.insurtech.backend.exception.ErrorCode;
import com.insurtech.backend.exception.NotFoundException;
import com.insurtech.backend.mapper.ClaimMapper;
import com.insurtech.backend.repository.ClaimRepository;
import com.insurtech.backend.repository.UserRepository;
import com.insurtech.backend.service.ClaimFileService;
import com.insurtech.backend.service.ClaimService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final UserRepository userRepository;
    private final ClaimFileService claimFileService;

    public List<ClaimResponse> getAll(UUID userId) {
        return claimMapper.toResponseList(
                claimRepository.findAllByUserId(userId)
                        .orElse(List.of()));
    }

    public ClaimResponse getByClaimNumber(String claimNumber) {
        return claimMapper.toResponse(
                claimRepository.findByClaimNumber((claimNumber))
                        .orElseThrow(() -> new NotFoundException(
                                ErrorCode.NOT_FOUND, "Claim not found. claimNumber" + claimNumber)));
    }

    @Transactional
    public ClaimResponse create(UUID userId, ClaimRequest data, List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "User not found. id: {}" + userId));

        Claim claim = claimMapper.toEntity(data);
        claim.setUser(user);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claimRepository.save(claim);

        claimFileService.create(claim, files);

        return claimMapper.toResponse(claim);
    }

    @Transactional
    public void delete(String claimNumber) {
        Claim claim = claimRepository.findByClaimNumber(claimNumber)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.NOT_FOUND, "Claim not found. claimNumber" + claimNumber));
        claimFileService.delete(claim);
        claimRepository.delete(claim);
    }
}
