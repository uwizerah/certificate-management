package com.seccertificate.api.repository;

import com.seccertificate.api.domain.CertificateTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateTemplateRepository
        extends JpaRepository<CertificateTemplate, Long> {

    List<CertificateTemplate> findByCustomerId(Long customerId);
}
