package com.seccertificate.api.security;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.service.ApiKeyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;

    public ApiKeyFilter(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    // Skip public endpoints (customer onboarding, verify, and CORS preflight)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) return true; // CORS preflight

        // open endpoints
        if ("POST".equalsIgnoreCase(method) && "/api/customers".equals(uri)) return true; // onboarding
        if ("GET".equalsIgnoreCase(method) && (uri.startsWith("/api/verify/") || uri.startsWith("/api/certificates/verify/")))
            return true; // public verification (support either path)

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // header name is case-insensitive, Servlet API normalizes; keep canonical form
        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || apiKey.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing API Key");
            return;
        }

        Customer customer = apiKeyService.validateKey(apiKey);
        if (customer == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API Key");
            return;
        }

        // Put customer into Spring Security context
        Authentication auth = new UsernamePasswordAuthenticationToken(customer, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Also attach for controllers/services that use @RequestAttribute
        request.setAttribute("authedCustomer", customer);

        filterChain.doFilter(request, response);
    }
}
