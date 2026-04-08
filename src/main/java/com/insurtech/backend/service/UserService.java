package com.insurtech.backend.service;

import com.insurtech.backend.domain.entity.User;

public interface UserService {
  User getUser(String email);
}
