package com.seccertificate.api.service;

import com.seccertificate.api.repository.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    CertificateRepository repo;

    @InjectMocks
    VerificationService service;

    @Test
    void verify_shouldReturnTrue_whenHashExists() {
        when(repo.existsByVerificationHash("abc123")).thenReturn(true);

        boolean result = service.verify("abc123");

        assertTrue(result);
        verify(repo).existsByVerificationHash("abc123");
    }

    @Test
    void verify_shouldReturnFalse_whenHashDoesNotExist() {
        when(repo.existsByVerificationHash("missing")).thenReturn(false);

        boolean result = service.verify("missing");

        assertFalse(result);
        verify(repo).existsByVerificationHash("missing");
    }
}
