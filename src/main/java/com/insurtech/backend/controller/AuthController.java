package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import com.insurtech.backend.dto.api.request.LoginRequestDto;
import com.insurtech.backend.dto.api.request.RegistrationRequestDto;
import com.insurtech.backend.dto.api.response.AuthResponseDto;
import com.insurtech.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthController.URL)
@RequiredArgsConstructor
public class AuthController {
    public static final String URL = ApiConstants.BASE_URL + "/auth";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @ModelAttribute RegistrationRequestDto request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
