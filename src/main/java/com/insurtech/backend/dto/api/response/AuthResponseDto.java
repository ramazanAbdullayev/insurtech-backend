package com.insurtech.backend.dto.api.response;

import lombok.Builder;

@Builder
public record AuthResponseDto(String token) {}
