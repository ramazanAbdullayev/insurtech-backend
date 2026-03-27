package com.insurtech.backend.dto.api.response;

import lombok.Builder;

@Builder
public record AuthResponse(String token) {}
