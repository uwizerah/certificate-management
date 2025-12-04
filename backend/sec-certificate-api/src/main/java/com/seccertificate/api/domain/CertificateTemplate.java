package com.seccertificate.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "certificate_templates")
@Getter
@Setter
public class CertificateTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String htmlTemplate;

    @ManyToOne(optional = false)
    private Customer customer;

    private Instant createdAt = Instant.now();

    @ElementCollection
    @Column(name = "placeholder")
    private List<String> placeholders = new ArrayList<>();
}
