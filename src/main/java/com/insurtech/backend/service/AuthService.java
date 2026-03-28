package com.insurtech.backend.service;

import com.insurtech.backend.dto.api.request.LoginRequest;
import com.insurtech.backend.dto.api.request.RegisterRequest;
import com.insurtech.backend.dto.api.response.AuthResponse;

public interface AuthService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
