package com.insurtech.backend.service.impl;

import com.insurtech.backend.repository.ClaimRepository;
import com.insurtech.backend.service.ClaimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;


}
