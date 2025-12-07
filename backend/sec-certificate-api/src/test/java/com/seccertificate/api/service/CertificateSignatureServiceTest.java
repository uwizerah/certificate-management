package com.seccertificate.api.service;

import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CertificateSignatureServiceTest {

    private final String SECRET = "test-secret-key";

    private CertificateSignatureService newService() {
        return new CertificateSignatureService(SECRET);
    }

    private Certificate baseCert() {
        Customer c = new Customer();
        c.setId(1L);

        CertificateTemplate t = new CertificateTemplate();
        t.setId(2L);

        Certificate cert = new Certificate();
        cert.setId(10L);
        cert.setCustomer(c);
        cert.setTemplate(t);
        cert.setIssuedTo("Alice");
        cert.setDataJson("{\"course\":\"Math\"}");
        cert.setVerificationHash("hash123");

        return cert;
    }

    // ✅ Constructor validation
    @Test
    void constructor_requiresSecret() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> new CertificateSignatureService("")
        );

        assertEquals("sec.signature.secret must be configured", ex.getMessage());
    }

    // ✅ Signature is generated
    @Test
    void computeSignature_generatesValue() {
        CertificateSignatureService service = newService();

        String sig = service.computeSignature(baseCert());

        assertNotNull(sig);
        assertFalse(sig.isBlank());
        assertTrue(sig.length() > 10);
    }

    // ✅ Sign sets signature and timestamp
    @Test
    void sign_setsSignatureAndTimestamp() {
        CertificateSignatureService service = newService();
        Certificate cert = baseCert();

        service.sign(cert);

        assertNotNull(cert.getSignature());
        assertNotNull(cert.getSignedAt());
    }

    // ✅ Verify passes for valid cert
    @Test
    void verify_returnsTrue_whenValid() {
        CertificateSignatureService service = newService();
        Certificate cert = baseCert();

        service.sign(cert);

        boolean result = service.verify(cert);

        assertTrue(result);
    }

    // ✅ Verify fails if certificate changes after signing
    @Test
    void verify_returnsFalse_ifDataChanges() {
        CertificateSignatureService service = newService();
        Certificate cert = baseCert();

        service.sign(cert);

        // Simulate tampering
        cert.setIssuedTo("Hacker");

        assertFalse(service.verify(cert));
    }

    // ✅ Verify fails if signature missing
    @Test
    void verify_returnsFalse_ifSignatureMissing() {
        CertificateSignatureService service = newService();
        Certificate cert = baseCert();

        assertFalse(service.verify(cert));
    }

    // ✅ Cannot sign without ID
    @Test
    void sign_throws_ifIdMissing() {
        CertificateSignatureService service = newService();
        Certificate cert = baseCert();
        cert.setId(null);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.sign(cert)
        );

        assertEquals("Cannot sign certificate without ID", ex.getMessage());
    }

    //buildPayload fails when JSON serialization fails
    @Test
    void computeSignature_throws_whenPayloadBuildFails() throws Exception {

        CertificateSignatureService service = newService();
        Certificate cert = baseCert();

        // Replace ObjectMapper with one that ALWAYS fails
        var mapperField = CertificateSignatureService.class.getDeclaredField("objectMapper");
        mapperField.setAccessible(true);

        mapperField.set(service, new com.fasterxml.jackson.databind.ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
                throw new com.fasterxml.jackson.core.JsonProcessingException("BOOM") {};
            }
        });

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.computeSignature(cert)
        );

        assertEquals("Signature computation failed", ex.getMessage());
    }

    //verify fails if signature manually changed
    @Test
    void verify_returnsFalse_whenSignatureModifiedManually() {

        CertificateSignatureService service = newService();
        Certificate cert = baseCert();

        service.sign(cert);

        // Force wrong signature
        cert.setSignature("FAKE_SIGNATURE");

        assertFalse(service.verify(cert));
    }
}
