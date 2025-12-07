package com.seccertificate.api.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class VerificationResultDto {

    /**
     * True if:
     *  - certificate exists
     *  - signature is present
     *  - signature matches current data
     */
    boolean valid;

    /**
     * True if the certificate was found but the signature does NOT match.
     * (data or database row has likely been tampered with)
     */
    boolean tampered;

    /**
     * Human-readable message suitable for the frontend.
     */
    String message;

    String verificationHash;
    String issuedTo;
    Instant issuedAt;
    String status;
    String templateName;
    String customerName;
}
