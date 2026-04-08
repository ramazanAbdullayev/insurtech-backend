package com.insurtech.backend.service.impl;

import com.insurtech.backend.repository.ClaimEstimationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimEstimationService {
  private final ClaimEstimationRepository claimEstimationRepository;
}
