package com.seccertificate.api.service;

import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.TemplateRequest;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import com.seccertificate.api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemplateService {

    private final CertificateTemplateRepository templateRepo;
    private final CustomerRepository customerRepo;

    public TemplateService(CertificateTemplateRepository templateRepo,
                           CustomerRepository customerRepo) {
        this.templateRepo = templateRepo;
        this.customerRepo = customerRepo;
    }

    // New: for authed customer (preferred path from UI/API key)
    public CertificateTemplate createTemplate(Customer customer, TemplateRequest req) {
        CertificateTemplate t = new CertificateTemplate();
        t.setName(req.getName());
        t.setHtmlTemplate(req.getHtmlTemplate());
        t.setCustomer(customer);
        return templateRepo.save(t);
    }

    // Keep existing method (used by admin tooling if you still call with ID)
    public CertificateTemplate createTemplate(Long customerId, CertificateTemplate template) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        template.setCustomer(customer);
        return templateRepo.save(template);
    }

    public List<CertificateTemplate> getByCustomer(Long customerId) {
        return templateRepo.findByCustomerId(customerId);
    }
}
