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
import com.insurtech.backend.service.ClaimService;
import com.insurtech.backend.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ClaimMapper claimMapper;
    private final UserRepository userRepository;

    public List<ClaimResponse> getAll(UUID userId) {
        List<Claim> claims = claimRepository.findAllByUserId(userId)
                .orElse(List.of());
        return claimMapper.toResponseList(claims);
    }

    @Transactional
    public ClaimResponse create(UUID userId, ClaimRequest data, List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, "User not found. id: {}" + userId));

        Claim claim = claimMapper.toEntity(data);
        claim.setUser(user);
        claim.setStatus(ClaimStatus.SUBMITTED);
        claimRepository.save(claim);

        return claimMapper.toResponse(claim);
    }

}
