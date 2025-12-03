package com.seccertificate.api.controller;

import com.seccertificate.api.service.VerificationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verify")
public class VerificationController {

    private final VerificationService service;

    public VerificationController(VerificationService service) {
        this.service = service;
    }

    @GetMapping("/{hash}")
    public boolean verify(@PathVariable String hash) {
        return service.verify(hash);
    }
}
