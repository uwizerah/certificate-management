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
import java.nio.file.Files;
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
    private final CertificateSignatureService signatureService;
    private final ObjectMapper mapper;

    public CertificateService(CertificateRepository certRepo,
                              CertificateTemplateRepository templateRepo,
                              PdfService pdfService,
                              ObjectMapper mapper,
                              CertificateSignatureService signatureService) {
        this.certRepo = certRepo;
        this.templateRepo = templateRepo;
        this.pdfService = pdfService;
        this.mapper = mapper;
        this.signatureService = signatureService;
    }

    public Certificate generate(Customer customer, Long templateId, Map<String,String> data) throws Exception {
        CertificateTemplate template = templateRepo.findById(templateId).orElseThrow();

        if (!template.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized template usage");
        }

        //ENFORCE PLACEHOLDERS
        List<String> required = template.getPlaceholders();
        for (String key : required) {
            if (!data.containsKey(key) || data.get(key) == null || data.get(key).trim().isEmpty()) {
                throw new IllegalArgumentException("Missing required field: " + key);
            }
        }

        // 1. Save base certificate
        Certificate cert = new Certificate();
        cert.setCustomer(customer);
        cert.setTemplate(template);
        cert.setIssuedTo(inferIssuedTo(data));
        cert.setDataJson(mapper.writeValueAsString(data));
        cert.setStatus("PENDING");
        cert.setVerificationHash(hash(customer.getId() + "-" + templateId + "-" + System.nanoTime()));

        Certificate saved = certRepo.save(cert);

        // 2. Generate PDF
        pdfService.generatePdf(saved);

        // 3. Reload (FINAL state)
        Certificate finalCert = certRepo.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("PDF generation failed"));

        if (finalCert.getPdfPath() == null) {
            throw new RuntimeException("PDF not generated");
        }

        // 4. SIGN AFTER FINAL STATE
        signatureService.sign(finalCert);
        certRepo.save(finalCert);

        return finalCert;
    }

    public Resource downloadFor(Customer customer, Long id) throws Exception {
        Certificate cert = certRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!cert.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Unauthorized certificate access");
        }

        if (cert.getPdfPath() == null || cert.getPdfPath().isBlank()) {
            throw new RuntimeException("PDF not yet generated");
        }

        Path path = Path.of(cert.getPdfPath());
        if (!Files.exists(path)) {
            throw new RuntimeException("PDF missing on server");
        }

        return new UrlResource(path.toUri());
    }

    public List<Certificate> listFor(Customer customer) {
        return certRepo.findByCustomerIdOrderByCreatedAtDesc(customer.getId());
    }


    private String hash(String input) throws Exception {
        return HexFormat.of().formatHex(
                MessageDigest.getInstance("SHA-256")
                        .digest(input.getBytes(StandardCharsets.UTF_8))
        );
    }

    private String inferIssuedTo(Map<String, String> data) {

        List<String> candidateKeys = List.of(
            "name", "recipient", "student", "client",
            "full_name", "fullname", "user", "candidate", "person"
        );

        for (String key : candidateKeys) {
            for (String actual : data.keySet()) {
                if (actual.equalsIgnoreCase(key)) {
                    return data.get(actual);
                }
            }
        }

        // Fallback if nothing semantic is found
        return "UNKNOWN";
    }
}
