package com.seccertificate.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CertificateResponse {

    private Long id;
    private String issuedTo;
    private String status;
    private String verificationHash;
    private Instant createdAt;
}
