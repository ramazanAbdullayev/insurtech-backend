package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.exception.NotFoundException;
import com.insurtech.backend.repository.UserRepository;
import com.insurtech.backend.security.CustomUserDetails;
import com.insurtech.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadSecurityUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User with this email: '" + email + "' not found"));
        return new CustomUserDetails(user);
    }
}
