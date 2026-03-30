package com.insurtech.backend.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserDetails loadSecurityUser(String email);
}
