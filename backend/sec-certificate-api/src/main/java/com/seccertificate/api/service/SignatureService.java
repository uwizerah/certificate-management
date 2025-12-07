package com.seccertificate.api.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class SignatureService {

    private static final String SECRET = "CHANGE_THIS_SECRET_KEY"; // move later to env var
    private static final String ALGO = "HmacSHA256";

    public String sign(String data) {
        try {
            Mac mac = Mac.getInstance(ALGO);
            mac.init(new SecretKeySpec(SECRET.getBytes(), ALGO));
            byte[] hash = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Signing failed", e);
        }
    }

    public boolean verify(String data, String signature) {
        String expected = sign(data);
        return expected.equals(signature);
    }
}
