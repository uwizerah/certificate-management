package com.seccertificate.api.repository;

import com.seccertificate.api.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    // For verification API
    boolean existsByVerificationHash(String hash);
    Optional<Certificate> findByVerificationHash(String verificationHash);

    Optional<Certificate> findByIdAndCustomer_Id(Long id, Long customerId);
    List<Certificate> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

}
