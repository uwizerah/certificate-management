package com.seccertificate.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "certificates")
@Getter
@Setter
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-visible recipient name.
     */
    private String issuedTo;

    /**
     * JSON with all placeholder values used during generation.
     */
    @Column(columnDefinition = "TEXT")
    private String dataJson;

    /**
     * e.g. PENDING, GENERATED, REVOKED
     */
    private String status;

    /**
     * Path on disk or in storage where the generated PDF is stored.
     */
    private String pdfPath;

    /**
     * Public identifier used in URLs / QR codes for verification.
     * This can be exposed to end users.
     */
    @Column(unique = true, nullable = false)
    private String verificationHash;

    /**
     * Server-side cryptographic signature (HMAC) over the core certificate data.
     * This is NOT exposed to clients and is used to detect tampering.
     */
    @Column(columnDefinition = "TEXT")
    private String signature;

    /**
     * When the certificate was cryptographically signed.
     */
    private Instant signedAt;

    @ManyToOne(optional = false)
    private Customer customer;

    @ManyToOne(optional = false)
    private CertificateTemplate template;

    private Instant createdAt = Instant.now();
}
