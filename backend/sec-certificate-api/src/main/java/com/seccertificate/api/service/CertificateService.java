package com.seccertificate.api.service;

import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.repository.CertificateRepository;
import com.seccertificate.api.repository.CertificateTemplateRepository;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Map;
import java.util.List;
import com.seccertificate.api.dto.CertificateResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CertificateService {

    private final CertificateRepository certRepo;
    private final CertificateTemplateRepository templateRepo;
    private final PdfService pdfService;
    private final ObjectMapper mapper;

    public CertificateService(CertificateRepository certRepo,
                              CertificateTemplateRepository templateRepo,
                              PdfService pdfService,
                              ObjectMapper mapper) {
        this.certRepo = certRepo;
        this.templateRepo = templateRepo;
        this.pdfService = pdfService;
        this.mapper = mapper;
    }

    public Certificate generate(Customer customer, Long templateId, Map<String,String> data) throws Exception {
        CertificateTemplate template = templateRepo.findById(templateId).orElseThrow();

        if (!template.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized template usage");
        }

        Certificate cert = new Certificate();
        cert.setCustomer(customer);
        cert.setTemplate(template);
        cert.setIssuedTo(data.get("name"));
        cert.setDataJson(mapper.writeValueAsString(data));
        cert.setStatus("PENDING");
        cert.setVerificationHash(hash(customer.getId() + "-" + templateId + "-" + data.toString()));

        Certificate saved = certRepo.save(cert);
        pdfService.generatePdfAsync(saved);
        return saved;
    }

    public Resource downloadFor(Customer customer, Long id) throws Exception {
        Certificate cert = certRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!cert.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized certificate access");
        }

        Path path = Path.of(cert.getPdfPath());
        return new UrlResource(path.toUri());
    }

    private String hash(String input) throws Exception {
        return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8))
        );
    }

    public List<Certificate> listFor(Customer customer) {
        return certRepo.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
    }
}
