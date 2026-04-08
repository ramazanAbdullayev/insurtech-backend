package com.insurtech.backend.service;

import com.insurtech.backend.domain.entity.Claim;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClaimFileService {
    void create(Claim claim, List<MultipartFile> files);
    void delete(Claim claim);
}
