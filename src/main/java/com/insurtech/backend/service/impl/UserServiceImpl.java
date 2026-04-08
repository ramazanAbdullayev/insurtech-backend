package com.insurtech.backend.service.impl;

import com.insurtech.backend.domain.entity.User;
import com.insurtech.backend.exception.ErrorCode;
import com.insurtech.backend.exception.NotFoundException;
import com.insurtech.backend.repository.UserRepository;
import com.insurtech.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public User getUser(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new NotFoundException(ErrorCode.NOT_FOUND, "User not found. email: " + email));
  }
}
