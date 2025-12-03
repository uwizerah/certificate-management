package com.seccertificate.api.service;

import com.seccertificate.api.repository.CertificateRepository;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    private final CertificateRepository repo;

    public VerificationService(CertificateRepository repo) {
        this.repo = repo;
    }

    public boolean verify(String hash) {
        return repo.existsByVerificationHash(hash);
    }
}
