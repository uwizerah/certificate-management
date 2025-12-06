package com.seccertificate.api.controller;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.service.ApiKeyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiKeyService apiKeyService;

    @Test
    void me_shouldReturnCustomerName() throws Exception {

        Customer customer = new Customer();
        customer.setName("Alice");

        // ✅ Authenticated principal with role
        TestingAuthenticationToken auth =
                new TestingAuthenticationToken(customer, null, "ROLE_USER");
        auth.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/api/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));

        SecurityContextHolder.clearContext();
    }
}
