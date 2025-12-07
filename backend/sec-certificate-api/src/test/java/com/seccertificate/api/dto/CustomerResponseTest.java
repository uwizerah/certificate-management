package com.seccertificate.api.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerResponseTest {

    @Test
    void constructor_shouldSetFieldsCorrectly() {

        CustomerResponse dto = new CustomerResponse(1L, "ACME", "key-123");

        assertEquals(1L, dto.getId());
        assertEquals("ACME", dto.getName());
        assertEquals("key-123", dto.getApiKey());
    }

    @Test
    void setters_shouldUpdateValuesCorrectly() {

        CustomerResponse dto = new CustomerResponse();

        dto.setId(2L);
        dto.setName("XYZ Corp");
        dto.setApiKey("apikey-456");

        assertEquals(2L, dto.getId());
        assertEquals("XYZ Corp", dto.getName());
        assertEquals("apikey-456", dto.getApiKey());
    }

    @Test
    void defaultConstructor_shouldInitializeWithNulls() {

        CustomerResponse dto = new CustomerResponse();

        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getApiKey());
    }
}
