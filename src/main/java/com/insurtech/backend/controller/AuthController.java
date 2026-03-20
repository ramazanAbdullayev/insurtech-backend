package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthController.URL)
@RequiredArgsConstructor
public class AuthController {
    public static final String URL = ApiConstants.BASE_URL + "/auth";
}
