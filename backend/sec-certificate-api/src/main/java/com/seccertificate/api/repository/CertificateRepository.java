package com.seccertificate.api.repository;

import com.seccertificate.api.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    // For verification API
    boolean existsByVerificationHash(String hash);

    // Optional: fetch certificate by hash if needed later
    Optional<Certificate> findByVerificationHash(String hash);

    List<Certificate> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

}
