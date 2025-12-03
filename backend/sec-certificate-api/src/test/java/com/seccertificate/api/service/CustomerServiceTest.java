package com.seccertificate.api.service;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.CustomerRequest;
import com.seccertificate.api.dto.CustomerResponse;
import com.seccertificate.api.exception.BadRequestException;
import com.seccertificate.api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    // --- create(CustomerRequest req) Tests ---
    
    @Test
    void create_ShouldThrowBadRequestException_WhenNameIsMissing() {
        // Arrange
        CustomerRequest request = new CustomerRequest();
        request.setName(null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            customerService.create(request);
        });
        
        // Verify that the repository was never called
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void create_ShouldReturnCustomerResponse_WhenSuccessful() {
        // Arrange
        CustomerRequest request = new CustomerRequest();
        request.setName("New Client Corp");

        // Use a Mockito Answer to capture the Customer object passed to save()
        // and return it, while simulating database ID generation.
        // This ensures the generated apiKey is present in the returned object.
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customerToSave = invocation.getArgument(0);
            customerToSave.setId(10L); // Simulate ID generation
            return customerToSave;
        });

        // Act
        CustomerResponse response = customerService.create(request);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("New Client Corp", response.getName());
        assertNotNull(response.getApiKey());
        
        // Verify that the save was called once, and the Customer object passed has the correct name
        verify(customerRepository, times(1)).save(argThat(customer -> 
            customer.getName().equals("New Client Corp") && customer.getApiKey() != null
        ));
    }
    
    // --- list() Tests ---

    @Test
    void list_ShouldReturnEmptyList_WhenNoCustomersExist() {
        // Arrange
        when(customerRepository.findAll()).thenReturn(List.of());

        // Act
        List<CustomerResponse> result = customerService.list();

        // Assert
        assertTrue(result.isEmpty());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void list_ShouldReturnListOfCustomerResponses() {
        // Arrange
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setName("Cust 1");
        c1.setApiKey("key1");

        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setName("Cust 2");
        c2.setApiKey("key2");
        
        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));

        // Act
        List<CustomerResponse> result = customerService.list();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Cust 1", result.get(0).getName());
        assertEquals("key2", result.get(1).getApiKey());
        verify(customerRepository, times(1)).findAll();
    }
}