package com.seccertificate.api.controller;

import com.seccertificate.api.dto.VerificationResultDto;
import com.seccertificate.api.service.ApiKeyService;
import com.seccertificate.api.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebMvcTest(VerificationController.class)
@AutoConfigureMockMvc(addFilters = false)
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private ApiKeyService apiKeyService;

    @Test
    void verify_shouldReturnValidPayload() throws Exception {

        VerificationResultDto dto = VerificationResultDto.builder()
                .valid(true)
                .tampered(false)
                .message("Certificate is valid")
                .verificationHash("abc")
                .issuedTo("John")
                .issuedAt(Instant.now())
                .status("GENERATED")
                .templateName("Completion")
                .customerName("Acme Corp")
                .build();

        when(verificationService.verify("abc")).thenReturn(dto);

        mockMvc.perform(get("/api/verify/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.issuedTo").value("John"))
                .andExpect(jsonPath("$.customerName").value("Acme Corp"));
    }

    @Test
    void verify_shouldReturnNotFoundPayload() throws Exception {

        VerificationResultDto dto = VerificationResultDto.builder()
                .valid(false)
                .tampered(false)
                .message("No certificate found for this verification hash.")
                .verificationHash("missing")
                .build();

        when(verificationService.verify("missing")).thenReturn(dto);

        mockMvc.perform(get("/api/verify/missing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.message").value("No certificate found for this verification hash."));
    }
}
