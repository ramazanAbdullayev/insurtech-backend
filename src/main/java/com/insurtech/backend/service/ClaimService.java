package com.insurtech.backend.service;

import com.insurtech.backend.dto.api.request.ClaimRequest;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ClaimService {

  List<ClaimResponse> getAll(UUID userId);

  ClaimResponse getByClaimNumber(String claimNumber);

  ClaimResponse create(UUID userId, ClaimRequest data, List<MultipartFile> files);

  void delete(String claimNumber);
}
