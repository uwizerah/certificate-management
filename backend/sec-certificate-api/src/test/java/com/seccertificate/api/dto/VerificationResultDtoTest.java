package com.seccertificate.api.dto;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class VerificationResultDtoTest {

    @Test
    void equals_shouldReturnTrue_forSameValues() {
        Instant now = Instant.now();

        VerificationResultDto a = VerificationResultDto.builder()
                .valid(true)
                .tampered(false)
                .message("OK")
                .verificationHash("abc")
                .issuedTo("John")
                .issuedAt(now)
                .status("GENERATED")
                .templateName("Completion")
                .customerName("ACME")
                .build();

        VerificationResultDto b = VerificationResultDto.builder()
                .valid(true)
                .tampered(false)
                .message("OK")
                .verificationHash("abc")
                .issuedTo("John")
                .issuedAt(now)
                .status("GENERATED")
                .templateName("Completion")
                .customerName("ACME")
                .build();

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void equals_shouldReturnFalse_whenDifferentData() {
        VerificationResultDto a = VerificationResultDto.builder()
                .valid(true)
                .verificationHash("abc")
                .build();

        VerificationResultDto b = VerificationResultDto.builder()
                .valid(false)
                .verificationHash("xyz")
                .build();

        assertNotEquals(a, b);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithNull() {
        VerificationResultDto dto = VerificationResultDto.builder()
                .verificationHash("abc")
                .build();

        assertNotEquals(dto, null);
    }

    @Test
    void equals_shouldReturnFalse_whenComparedWithDifferentType() {
        VerificationResultDto dto = VerificationResultDto.builder()
                .verificationHash("abc")
                .build();

        assertNotEquals(dto, "abc");
    }

    @Test
    void toString_shouldNotBeEmpty() {
        VerificationResultDto dto = VerificationResultDto.builder()
                .valid(true)
                .tampered(false)
                .message("Valid")
                .verificationHash("abc")
                .issuedTo("John")
                .issuedAt(Instant.now())
                .status("GENERATED")
                .templateName("Completion")
                .customerName("ACME")
                .build();

        String output = dto.toString();

        assertNotNull(output);
        assertTrue(output.contains("VerificationResultDto"));
        assertTrue(output.contains("abc"));
        assertTrue(output.contains("John"));
    }

    @Test
    void builder_shouldSetAllFieldsCorrectly() {
        Instant now = Instant.now();

        VerificationResultDto dto = VerificationResultDto.builder()
                .valid(true)
                .tampered(true)
                .message("Broken")
                .verificationHash("zzz")
                .issuedTo("Mary")
                .issuedAt(now)
                .status("REVOKED")
                .templateName("Training")
                .customerName("XYZ Corp")
                .build();

        assertTrue(dto.isValid());
        assertTrue(dto.isTampered());
        assertEquals("Broken", dto.getMessage());
        assertEquals("zzz", dto.getVerificationHash());
        assertEquals("Mary", dto.getIssuedTo());
        assertEquals(now, dto.getIssuedAt());
        assertEquals("REVOKED", dto.getStatus());
        assertEquals("Training", dto.getTemplateName());
        assertEquals("XYZ Corp", dto.getCustomerName());
    }
}
