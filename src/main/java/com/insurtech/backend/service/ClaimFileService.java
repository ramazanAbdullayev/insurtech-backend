package com.insurtech.backend.service;

import com.insurtech.backend.domain.entity.Claim;
import com.insurtech.backend.domain.entity.ClaimFile;
import com.insurtech.backend.dto.api.response.ClaimFileResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface ClaimFileService {
  List<ClaimFileResponse> getByClaimId(UUID claimId);

  List<ClaimFile> getByClaim(Claim claim);

  void upload(Claim claim, MultipartFile file);

  boolean deleteFromStorage(UUID claimFileId);
}
