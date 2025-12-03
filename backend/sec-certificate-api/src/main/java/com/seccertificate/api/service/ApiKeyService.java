package com.seccertificate.api.service;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    private final CustomerRepository repo;

    public ApiKeyService(CustomerRepository repo) {
        this.repo = repo;
    }

    public Customer validateKey(String apiKey) {
        return repo.findByApiKey(apiKey)
                .orElseThrow(() -> new RuntimeException("Invalid API key"));
    }
}