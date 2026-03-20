package com.insurtech.backend.service.impl;

import com.insurtech.backend.constants.enums.api.UserRole;
import com.insurtech.backend.constants.enums.api.UserStatus;
import com.insurtech.backend.dto.api.request.LoginRequestDto;
import com.insurtech.backend.dto.api.request.RegistrationRequestDto;
import com.insurtech.backend.dto.api.response.AuthResponseDto;
import com.insurtech.backend.entity.UserEntity;
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
    public void register(RegistrationRequestDto request) {
        if (userRepository.existsByEmail(request.email())) throw new AlreadyExistException(
                    "User with this '%s' email already exist.".formatted(request.email()));

        userRepository.save(UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .role(UserRole.CLIENT)
                .status(UserStatus.ACTIVE)
                .passwordHash(request.password()) // need to be hashed !!!!!
                .build());
    }

    @Override
    public AuthResponseDto login(LoginRequestDto request) {
        String hashedUserPassword = userRepository.findByEmail(request.email());
        String hashedLoginPassword = request.password(); // need to be hashed

        if (!hashedUserPassword.equals(hashedLoginPassword) ||
                StringUtils.isEmpty(hashedUserPassword))
            throw new InvalidCredentialsException("Email or password is invalid!");

        return AuthResponseDto.builder()
                .token("Need to be generated !!!!! ")
                .build();
    }
}
