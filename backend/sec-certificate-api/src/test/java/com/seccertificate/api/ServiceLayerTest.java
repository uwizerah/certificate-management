package com.seccertificate.api;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.CustomerRequest;
import com.seccertificate.api.dto.CustomerResponse;
import com.seccertificate.api.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ServiceLayerTest {

    @Autowired
    private CustomerService customerService;

    @Test
    void apiKeyIsGenerated() {
        CustomerRequest request = new CustomerRequest();
        request.setName("TestCo");
        CustomerResponse customer = customerService.create(request);
        assertNotNull(customer.getApiKey());
    }
}
