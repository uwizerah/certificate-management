package com.seccertificate.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seccertificate.api.dto.CustomerRequest;
import com.seccertificate.api.dto.CustomerResponse;
import com.seccertificate.api.service.CustomerService;
import com.seccertificate.api.service.ApiKeyService; // Needed for @MockBean
import com.seccertificate.api.security.ApiKeyFilter; // Needed for excludeFilters
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
// Re-include the CSRF import!
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = CustomerController.class,
    // FIX: Exclude the custom ApiKeyFilter to bypass the 401 Unauthorized error.
    excludeFilters = @Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = ApiKeyFilter.class
    )
)
// Use @WithMockUser to provide the authenticated principal (Admin role) for access control.
@WithMockUser(roles = "ADMIN")
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // We mock the service layer to isolate the controller logic
    @MockBean
    private CustomerService customerService;

    // This is still required to satisfy the dependency of the ApiKeyFilter bean,
    // which is likely still being created but not added to the security chain.
    @MockBean
    private ApiKeyService apiKeyService;

    // --- POST /api/customers Tests ---

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        // Arrange
        CustomerRequest request = new CustomerRequest();
        request.setName("Test Customer");
        
        CustomerResponse expectedResponse = new CustomerResponse(
            1L, "Test Customer", "test-api-key-123"
        );

        when(customerService.create(any(CustomerRequest.class))).thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(post("/api/customers")
                // 🔑 RE-APPLIED FIX: Add CSRF token to bypass the 403 Forbidden error for POST requests.
                .with(csrf()) 
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Customer"))
                .andExpect(jsonPath("$.apiKey").value("test-api-key-123"));
    }

    // --- GET /api/customers Tests ---

    @Test
    void listCustomers_ShouldReturnListOfCustomers() throws Exception {
        // Arrange
        CustomerResponse c1 = new CustomerResponse(
            1L, "Customer A", "key-a"
        );
        CustomerResponse c2 = new CustomerResponse(
            2L, "Customer B", "key-b"
        );
        List<CustomerResponse> expectedList = List.of(c1, c2);

        when(customerService.list()).thenReturn(expectedList);

        // Act & Assert
        // This test now passes by relying solely on @WithMockUser, since ApiKeyFilter is excluded.
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Customer A"))
                .andExpect(jsonPath("$[1].name").value("Customer B"));
    }
}