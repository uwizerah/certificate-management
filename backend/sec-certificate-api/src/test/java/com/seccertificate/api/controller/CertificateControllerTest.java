package com.seccertificate.api.controller;

import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.CertificateRequest;
import com.seccertificate.api.service.ApiKeyService;
import com.seccertificate.api.service.CertificateService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CertificateController.class)
@AutoConfigureMockMvc(addFilters = false)
class CertificateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService certificateService;

    // ✅ REQUIRED because ApiKeyFilter depends on it
    @MockBean
    private ApiKeyService apiKeyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer fakeUser() {
        Customer c = new Customer();
        c.setId(1L);
        return c;
    }

    // ✅ GENERATE TEST
    @Test
    void generate_shouldReturnCertificate() throws Exception {

        Certificate cert = new Certificate();
        cert.setId(10L);
        cert.setIssuedTo("Alice");
        cert.setStatus("READY");

        when(certificateService.generate(any(), any(), any()))
                .thenReturn(cert);

        CertificateRequest req = new CertificateRequest();
        req.setData(Map.of("name", "Alice"));

        mockMvc.perform(post("/api/certificates/generate")
                .requestAttr("customer", fakeUser())
                .param("templateId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.issuedTo").value("Alice"));
    }

    // ✅ LIST TEST
    @Test
    void list_shouldReturnCertificates() throws Exception {

        Certificate cert1 = new Certificate();
        cert1.setId(1L);
        cert1.setIssuedTo("Alice");

        when(certificateService.listFor(any()))
                .thenReturn(List.of(cert1));

        mockMvc.perform(get("/api/certificates")
                .requestAttr("customer", fakeUser()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].issuedTo").value("Alice"));
    }

    // ✅ DOWNLOAD TEST
    @Test
    void download_shouldReturnPdf() throws Exception {

        ByteArrayResource pdf = new ByteArrayResource("PDFDATA".getBytes());

        when(certificateService.downloadFor(any(), any()))
                .thenReturn(pdf);

        mockMvc.perform(get("/api/certificates/1/download")
                .requestAttr("customer", fakeUser()))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"certificate.pdf\""));
    }
}
