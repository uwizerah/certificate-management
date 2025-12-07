package com.seccertificate.api.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignatureServiceTest {

    SignatureService service = new SignatureService();

    // ✅ SIGNATURE SHOULD BE GENERATED
    @Test
    void sign_shouldReturnSignature() {

        String data = "hello-world";

        String signature = service.sign(data);

        assertNotNull(signature);
        assertFalse(signature.isBlank());
    }

    // ✅ VERIFY SHOULD RETURN TRUE FOR VALID DATA
    @Test
    void verify_shouldReturnTrueForCorrectSignature() {

        String data = "secure-message";

        String signature = service.sign(data);

        boolean valid = service.verify(data, signature);

        assertTrue(valid);
    }

    // ✅ VERIFY SHOULD RETURN FALSE FOR WRONG DATA
    @Test
    void verify_shouldReturnFalseForTamperedData() {

        String data = "secure-message";
        String wrong = "hacked-message";

        String signature = service.sign(data);

        boolean result = service.verify(wrong, signature);

        assertFalse(result);
    }

    // ✅ VERIFY SHOULD RETURN FALSE FOR WRONG SIGNATURE
    @Test
    void verify_shouldReturnFalseForInvalidSignature() {

        String data = "secure-message";

        boolean result = service.verify(data, "fake-signature");

        assertFalse(result);
    }

    // ✅ SIGNATURE SHOULD BE DETERMINISTIC
    @Test
    void sign_shouldBeDeterministic() {

        String data = "same-message";

        String sig1 = service.sign(data);
        String sig2 = service.sign(data);

        assertEquals(sig1, sig2);
    }
}
