package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import com.insurtech.backend.dto.api.request.ClaimRequest;
import com.insurtech.backend.dto.api.response.ClaimResponse;
import com.insurtech.backend.security.CustomUserDetails;
import com.insurtech.backend.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ClaimController.URL)
@RequiredArgsConstructor
public class ClaimController {

    public static final String URL = ApiConstants.BASE_URL + "/claims";

    private final ClaimService claimService;

    @GetMapping
    public ResponseEntity<Object> getById(@RequestParam UUID id) {
        return ResponseEntity.ok(null);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClaimResponse>> getAll(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(claimService.getAll(UUID.fromString(jwt.getSubject())));
    }

    @PostMapping("/create")
    public ResponseEntity<ClaimResponse> create(
            @Valid @RequestPart("data") ClaimRequest data,
            @RequestPart("files") List<MultipartFile> files,
            @AuthenticationPrincipal Jwt jwt
            ) {
        return ResponseEntity.ok(claimService.create(UUID.fromString(jwt.getSubject()), data, files));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteById() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
