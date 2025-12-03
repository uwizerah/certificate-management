package com.seccertificate.api.service;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.CustomerRequest;
import com.seccertificate.api.dto.CustomerResponse;
import com.seccertificate.api.exception.BadRequestException;
import com.seccertificate.api.repository.CustomerRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public CustomerResponse create(CustomerRequest req) {

        if (req.getName() == null || req.getName().isBlank()) {
            throw new BadRequestException("Customer name is required");
        }

        Customer c = new Customer();
        c.setName(req.getName());
        c.setApiKey(UUID.randomUUID().toString());

        Customer saved = repo.save(c);

        return new CustomerResponse(
            saved.getId(),
            saved.getName(),
            saved.getApiKey()
        );
    }

    public List<CustomerResponse> list() {
        return repo.findAll()
          .stream()
          .map(c -> new CustomerResponse(
              c.getId(), c.getName(), c.getApiKey()
          ))
          .toList();
    }
}

