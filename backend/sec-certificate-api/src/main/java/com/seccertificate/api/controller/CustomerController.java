package com.seccertificate.api.controller;

import com.seccertificate.api.dto.CustomerRequest;
import com.seccertificate.api.dto.CustomerResponse;
import com.seccertificate.api.service.CustomerService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    @PostMapping
    public CustomerResponse create(@RequestBody CustomerRequest req) {
        return service.create(req);
    }

    @GetMapping
    public List<CustomerResponse> list() {
        return service.list();
    }
}
