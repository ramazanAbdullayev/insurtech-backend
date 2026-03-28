package com.insurtech.backend.service;


import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.service.impl.RefreshTokenServiceImpl;

import java.util.UUID;

public interface RefreshTokenService {

    String issue(User user, UUID familyId, String userAgent, String ip);

    RefreshTokenServiceImpl.RotationResult rotate(String rawToken, String userAgent, String ip);

    void revokeToken(String rawToken);

    int revokeAllForUser(UUID userId);
}
