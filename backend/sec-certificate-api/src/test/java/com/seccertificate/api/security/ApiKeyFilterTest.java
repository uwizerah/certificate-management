package com.seccertificate.api.security;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApiKeyFilterTest {

    private ApiKeyService apiKeyService;
    private ApiKeyFilter filter;

    @BeforeEach
    void setup() {
        apiKeyService = mock(ApiKeyService.class);
        filter = new ApiKeyFilter(apiKeyService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipFilter_forCustomerOnboarding() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/api/customers");
        MockHttpServletResponse res = new MockHttpServletResponse();

        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldSkipFilter_forVerifyEndpoint() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/verify/abc123");
        MockHttpServletResponse res = new MockHttpServletResponse();

        assertTrue(filter.shouldNotFilter(req));
    }

    @Test
    void shouldReturn401_whenApiKeyMissing() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/anything");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(req, res, chain);

        assertEquals(401, res.getStatus());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticate_whenApiKeyIsValid() throws Exception {
        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/api/certificates");
        MockHttpServletResponse res = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Customer customer = new Customer();
        customer.setName("Alice");

        when(apiKeyService.validateKey("valid-key")).thenReturn(customer);

        req.addHeader("X-API-KEY", "valid-key");

        filter.doFilter(req, res, chain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(customer,
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
