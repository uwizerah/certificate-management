package com.seccertificate.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seccertificate.api.domain.Certificate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Base64;

@Service
@Slf4j
public class CertificateSignatureService {

    private static final String HMAC_ALG = "HmacSHA256";

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final SecretKeySpec secretKey;

    public CertificateSignatureService(
            @Value("${sec.signature.secret}") String secret
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("sec.signature.secret must be configured");
        }

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALG);
        log.info("CertificateSignatureService initialized");
    }

    /**
     * Canonical payload builder.
     * Do NOT change field ordering.
     */
    private String buildPayload(Certificate c) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("id", c.getId());
            payload.put("customerId", c.getCustomer() != null ? c.getCustomer().getId() : null);
            payload.put("templateId", c.getTemplate() != null ? c.getTemplate().getId() : null);
            payload.put("issuedTo", c.getIssuedTo());
            payload.put("dataJson", c.getDataJson());
            payload.put("verificationHash", c.getVerificationHash());

            String json = objectMapper.writeValueAsString(payload);
            log.debug("Signing payload: {}", json);
            return json;

        } catch (JsonProcessingException e) {
            log.error("Payload build failed", e);
            throw new IllegalStateException("Failed to build signing payload", e);
        }
    }

    public String computeSignature(Certificate cert) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALG);
            mac.init(secretKey);

            byte[] payloadBytes = buildPayload(cert).getBytes(StandardCharsets.UTF_8);
            byte[] signed = mac.doFinal(payloadBytes);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(signed);

        } catch (Exception e) {
            log.error("Signature computation failed", e);
            throw new IllegalStateException("Signature computation failed", e);
        }
    }

    /**
     * Sign certificate after PDF exists.
     */
    public void sign(Certificate cert) {
        if (cert.getId() == null) {
            throw new IllegalStateException("Cannot sign certificate without ID");
        }

        String signature = computeSignature(cert);
        cert.setSignature(signature);
        cert.setSignedAt(Instant.now());

        log.info("Certificate {} SIGNED", cert.getId());
    }

    /**
     * Verify certificate signature.
     */
    public boolean verify(Certificate cert) {

        if (cert.getSignature() == null) {
            log.error("Certificate {} has NO signature stored", cert.getId());
            return false;
        }

        String expected = computeSignature(cert);
        String actual = cert.getSignature();

        log.info("Verifying certificate {}", cert.getId());
        log.info("EXPECTED = {}", expected);
        log.info("ACTUAL   = {}", actual);

        boolean equal = MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                actual.getBytes(StandardCharsets.UTF_8)
        );

        if (!equal) {
            log.error("SIGNATURE MISMATCH for cert {}", cert.getId());
            log.warn("Expected (first 8): {}", expected.substring(0, 8));
            log.warn("Actual   (first 8): {}", actual.substring(0, 8));

            log.warn("Possible causes:");
            log.warn(" - Certificate changed after signing");
            log.warn(" - Signing secret changed");
            log.warn(" - Payload order changed");
        } else {
            log.info("Certificate {} SIGNATURE VALID", cert.getId());
        }

        return equal;
    }
}
