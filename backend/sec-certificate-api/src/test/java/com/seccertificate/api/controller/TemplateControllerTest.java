package com.seccertificate.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.dto.TemplateRequest;
import com.seccertificate.api.security.ApiKeyFilter;
import com.seccertificate.api.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
    controllers = TemplateController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = ApiKeyFilter.class)
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class TemplateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TemplateService templateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTemplate_shouldReturnTemplate() throws Exception {

        TemplateRequest req = new TemplateRequest();
        req.setName("Test Template");
        req.setHtmlTemplate("<h1>Test</h1>");

        CertificateTemplate template = new CertificateTemplate();
        template.setId(1L);
        template.setName("Test Template");

        Mockito.when(
            templateService.createTemplate(
                Mockito.any(com.seccertificate.api.domain.Customer.class),
                Mockito.any(TemplateRequest.class)
            )
        ).thenReturn(template);

        mockMvc.perform(post("/api/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Template"));
    }

    @Test
    void listTemplates_shouldReturnTemplates() throws Exception {

        CertificateTemplate t = new CertificateTemplate();
        t.setId(1L);
        t.setName("A");

        Mockito.when(templateService.getByCustomer(Mockito.any()))
                .thenReturn(List.of(t));

        mockMvc.perform(get("/api/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("A"));
    }
}
