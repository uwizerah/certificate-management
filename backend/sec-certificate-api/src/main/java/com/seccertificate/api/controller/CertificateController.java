package com.seccertificate.api.controller;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.dto.CertificateRequest;
import com.seccertificate.api.service.CertificateService;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.seccertificate.api.dto.CertificateResponse;


@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    private final CertificateService service;

    public CertificateController(CertificateService service) { this.service = service; }

    @PostMapping("/generate")
    public Certificate generate(@AuthenticationPrincipal Customer customer,
                                @RequestParam Long templateId,
                                @RequestBody CertificateRequest req) throws Exception {
        return service.generate(customer, templateId, req.getData());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id,
                                             @AuthenticationPrincipal Customer customer) throws Exception {
        // (optional but recommended) enforce ownership here as well
        Resource file = service.downloadFor(customer, id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"certificate.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @GetMapping
    public List<CertificateResponse> list(@AuthenticationPrincipal Customer customer) {
        return service.listFor(customer).stream()
            .map(c -> new CertificateResponse(c.getId(), c.getIssuedTo(), c.getStatus(), c.getVerificationHash(), c.getCreatedAt()))
            .toList();
    }
}
