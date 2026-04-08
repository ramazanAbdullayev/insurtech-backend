package com.insurtech.backend.service;

import com.insurtech.backend.domain.entity.Claim;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ClaimFileService {
  void create(Claim claim, List<MultipartFile> files);

  void delete(Claim claim);
}
