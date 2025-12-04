package com.seccertificate.api.service;

import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.TemplateRequest;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import com.seccertificate.api.repository.CustomerRepository;
import com.seccertificate.api.util.PlaceholderExtractor;
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

    public CertificateTemplate createTemplate(Customer customer, TemplateRequest req) {

        CertificateTemplate t = new CertificateTemplate();
        t.setName(req.getName());
        t.setHtmlTemplate(req.getHtmlTemplate());
        t.setCustomer(customer);
        t.setPlaceholders(
            PlaceholderExtractor.extract(req.getHtmlTemplate())
        );

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

    public CertificateTemplate getById(Customer customer, Long id) {
        CertificateTemplate t = templateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (!t.getCustomer().getId().equals(customer.getId())) {
            throw new RuntimeException("Access denied");
        }

        return t;
    }
}
