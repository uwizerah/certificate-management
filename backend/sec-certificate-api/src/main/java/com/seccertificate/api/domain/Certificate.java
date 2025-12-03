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

    private String issuedTo;

    @Column(columnDefinition = "TEXT")
    private String dataJson;

    private String status; // PENDING, GENERATED

    private String pdfPath;

    @Column(unique = true)
    private String verificationHash;

    @ManyToOne(optional = false)
    private Customer customer;

    @ManyToOne(optional = false)
    private CertificateTemplate template;

    private Instant createdAt = Instant.now();
}
