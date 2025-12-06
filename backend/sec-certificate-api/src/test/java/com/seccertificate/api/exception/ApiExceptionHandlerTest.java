package com.seccertificate.api.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404() {
        NotFoundException ex = new NotFoundException("Not found");

        ResponseEntity<String> response = handler.handleNotFound(ex);

        assertEquals(404, response.getStatusCode().value());
        assertEquals("Not found", response.getBody());
    }

    @Test
    void handleUnauthorized_shouldReturn401() {
        UnauthorizedException ex = new UnauthorizedException("Unauthorized");

        ResponseEntity<String> response = handler.handleUnauthorized(ex);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Unauthorized", response.getBody());
    }

    @Test
    void handleBadRequest_shouldReturn400() {
        BadRequestException ex = new BadRequestException("Bad request");

        ResponseEntity<String> response = handler.handleBadRequest(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("Bad request", response.getBody());
    }

    @Test
    void handleGeneral_shouldReturn500() {
        Exception ex = new Exception("Boom");

        ResponseEntity<String> response = handler.handleGeneral(ex);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Internal server error"));
        assertTrue(response.getBody().contains("Boom"));
    }
}
