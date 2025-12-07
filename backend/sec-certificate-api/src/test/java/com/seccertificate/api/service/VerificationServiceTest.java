package com.seccertificate.api.service;

import com.seccertificate.api.domain.*;
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

    //VALID CERTIFICATE
    @Test
    void verify_shouldReturnValid_whenHashExists_andSignatureValid() {

        Customer customer = new Customer();
        customer.setName("ACME");

        CertificateTemplate template = new CertificateTemplate();
        template.setName("Completion");

        Certificate cert = new Certificate();
        cert.setIssuedTo("John");
        cert.setStatus("GENERATED");
        cert.setCreatedAt(Instant.now());
        cert.setCustomer(customer);
        cert.setTemplate(template);

        when(repo.findByVerificationHash("abc")).thenReturn(Optional.of(cert));
        when(signatureService.verify(cert)).thenReturn(true);

        var result = service.verify("abc");

        assertTrue(result.isValid());
        assertFalse(result.isTampered());
        assertEquals("John", result.getIssuedTo());
        assertEquals("GENERATED", result.getStatus());
        assertEquals("ACME", result.getCustomerName());
        assertEquals("Completion", result.getTemplateName());
        assertEquals("abc", result.getVerificationHash());
        assertNotNull(result.getIssuedAt());

        verify(repo).findByVerificationHash("abc");
        verify(signatureService).verify(cert);
    }

    //TAMPERED CERTIFICATE
    @Test
    void verify_shouldReturnTampered_whenSignatureInvalid() {

        Customer customer = new Customer();
        customer.setName("XYZ Corp");

        CertificateTemplate template = new CertificateTemplate();
        template.setName("Training");

        Certificate cert = new Certificate();
        cert.setIssuedTo("Mary");
        cert.setStatus("REVOKED");
        cert.setCreatedAt(Instant.now());
        cert.setCustomer(customer);
        cert.setTemplate(template);

        when(repo.findByVerificationHash("bad-sig")).thenReturn(Optional.of(cert));
        when(signatureService.verify(cert)).thenReturn(false);

        var result = service.verify("bad-sig");

        assertFalse(result.isValid());
        assertTrue(result.isTampered());
        assertEquals("Mary", result.getIssuedTo());
        assertEquals("bad-sig", result.getVerificationHash());
        assertEquals("REVOKED", result.getStatus());
        assertEquals("XYZ Corp", result.getCustomerName());
        assertEquals("Training", result.getTemplateName());

        verify(repo).findByVerificationHash("bad-sig");
        verify(signatureService).verify(cert);
    }

    //NOT FOUND CERTIFICATE
    @Test
    void verify_shouldReturnNotFound_whenNoCertificate() {

        when(repo.findByVerificationHash("missing")).thenReturn(Optional.empty());

        var result = service.verify("missing");

        assertFalse(result.isValid());
        assertFalse(result.isTampered());
        assertEquals("No certificate found for this verification hash.", result.getMessage());
        assertEquals("missing", result.getVerificationHash());
        assertNull(result.getIssuedTo());
        assertNull(result.getStatus());
        assertNull(result.getTemplateName());
        assertNull(result.getCustomerName());

        verify(repo).findByVerificationHash("missing");
        verifyNoInteractions(signatureService);
    }
}
