package com.seccertificate.api.controller;

import com.seccertificate.api.service.ApiKeyService;
import com.seccertificate.api.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VerificationController.class)
@AutoConfigureMockMvc(addFilters = false)   // ✅ disables ApiKeyFilter + Security
class VerificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VerificationService verificationService;

    // still required so the context loads (ApiKeyFilter dependency)
    @MockBean
    private ApiKeyService apiKeyService;

    @Test
    void verify_shouldReturnTrue() throws Exception {
        when(verificationService.verify("abc")).thenReturn(true);

        mockMvc.perform(get("/api/verify/abc"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void verify_shouldReturnFalse() throws Exception {
        when(verificationService.verify("bad")).thenReturn(false);

        mockMvc.perform(get("/api/verify/bad"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}
