package com.seccertificate.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CertificateRequest {

    private Map<String, String> data;
}
