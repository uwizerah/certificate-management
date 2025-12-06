package com.seccertificate.api.service;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyServiceTest {

    @Mock
    private CustomerRepository repo;

    @InjectMocks
    private ApiKeyService service;

    @Test
    void validateKey_shouldReturnCustomer_whenValidKey() {
        Customer customer = new Customer();
        customer.setApiKey("abc123");

        when(repo.findByApiKey("abc123")).thenReturn(Optional.of(customer));

        Customer result = service.validateKey("abc123");

        assertEquals(customer, result);
        verify(repo).findByApiKey("abc123");
    }

    @Test
    void validateKey_shouldThrow_whenInvalidKey() {
        when(repo.findByApiKey("badkey")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.validateKey("badkey"));

        assertEquals("Invalid API key", ex.getMessage());
    }
}
