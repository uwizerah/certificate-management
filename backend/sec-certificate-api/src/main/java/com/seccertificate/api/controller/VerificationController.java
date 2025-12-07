package com.seccertificate.api.controller;

import com.seccertificate.api.dto.VerificationResultDto;
import com.seccertificate.api.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/{hash}")
    public ResponseEntity<VerificationResultDto> verify(@PathVariable String hash) {
        VerificationResultDto result = verificationService.verify(hash);
        // Always 200 with structured payload; frontend can check result.valid / tampered
        return ResponseEntity.ok(result);
    }
}
