package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.enums.UserStatus;
import com.insurtech.backend.dto.api.request.LoginRequest;
import com.insurtech.backend.dto.api.request.RegisterRequest;
import com.insurtech.backend.dto.api.response.AuthResponse;
import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.exception.AlreadyExistException;
import com.insurtech.backend.exception.InvalidCredentialsException;
import com.insurtech.backend.repository.UserRepository;
import com.insurtech.backend.service.AuthService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) throw new AlreadyExistException(
                    "User with this '%s' email already exist.".formatted(request.email()));

        userRepository.save(User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .passwordHash(request.password()) // need to be hashed !!!!!
                .build());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        String hashedUserPassword = userRepository.findByEmail(request.email());
        String hashedLoginPassword = request.password(); // need to be hashed

        if (!hashedUserPassword.equals(hashedLoginPassword) ||
                StringUtils.isEmpty(hashedUserPassword))
            throw new InvalidCredentialsException("Email or password is invalid!");

        return AuthResponse.builder()
                .token("Need to be generated !!!!! ")
                .build();
    }
}
