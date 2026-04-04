package com.insurtech.backend.controller;

import com.insurtech.backend.constants.ApiConstants;
import com.insurtech.backend.service.ClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<Object>> getAll() {
        return ResponseEntity.ok(null);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> create() {
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteById() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
