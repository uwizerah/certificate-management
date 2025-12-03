package com.seccertificate.api.controller;

import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.TemplateRequest;
import com.seccertificate.api.service.TemplateService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public CertificateTemplate create(
            @AuthenticationPrincipal Customer customer,
            @RequestBody TemplateRequest body) {
        return service.createTemplate(customer, body);
    }

    @GetMapping
    public List<CertificateTemplate> mine(@AuthenticationPrincipal Customer customer) {
        return service.getByCustomer(customer.getId());
    }
}
