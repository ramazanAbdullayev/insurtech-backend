package com.insurtech.backend.service;

import com.insurtech.backend.dto.api.request.LoginRequestDto;
import com.insurtech.backend.dto.api.request.RegistrationRequestDto;
import com.insurtech.backend.dto.api.response.AuthResponseDto;

public interface AuthService {
    void register(RegistrationRequestDto request);
    AuthResponseDto login(LoginRequestDto request);
}
