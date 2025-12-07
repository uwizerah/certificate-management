package com.seccertificate.api.service;

import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.repository.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationServiceTest {

    @Mock
    CertificateRepository repo;

    @Mock
    CertificateSignatureService signatureService;

    @InjectMocks
    VerificationService service;

    @Test
    void verify_shouldReturnValid_whenHashExists_andSignatureValid() {

        Certificate cert = new Certificate();
        cert.setIssuedTo("John");
        cert.setStatus("GENERATED");
        cert.setCreatedAt(Instant.now());

        when(repo.findByVerificationHash("abc")).thenReturn(Optional.of(cert));
        when(signatureService.verify(cert)).thenReturn(true);

        var result = service.verify("abc");

        assertTrue(result.isValid());
        assertFalse(result.isTampered());
        assertEquals("John", result.getIssuedTo());

        verify(repo).findByVerificationHash("abc");
        verify(signatureService).verify(cert);
    }

    @Test
    void verify_shouldReturnTampered_whenSignatureInvalid() {

        Certificate cert = new Certificate();
        cert.setIssuedTo("Mary");
        cert.setStatus("GENERATED");

        when(repo.findByVerificationHash("bad-sig")).thenReturn(Optional.of(cert));
        when(signatureService.verify(cert)).thenReturn(false);

        var result = service.verify("bad-sig");

        assertFalse(result.isValid());
        assertTrue(result.isTampered());
        assertEquals("Mary", result.getIssuedTo());

        verify(repo).findByVerificationHash("bad-sig");
        verify(signatureService).verify(cert);
    }

    @Test
    void verify_shouldReturnNotFound_whenNoCertificate() {

        when(repo.findByVerificationHash("missing")).thenReturn(Optional.empty());

        var result = service.verify("missing");

        assertFalse(result.isValid());
        assertFalse(result.isTampered());
        assertEquals("No certificate found for this verification hash.", result.getMessage());

        verify(repo).findByVerificationHash("missing");
        verifyNoInteractions(signatureService);
    }
}
