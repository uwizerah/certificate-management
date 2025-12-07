package com.seccertificate.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import com.seccertificate.api.repository.CustomerRepository;
import com.seccertificate.api.service.PdfService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CertificateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CertificateTemplateRepository templateRepo;

    // ✅ Mock PDF engine (avoid HTML → PDF + file IO + signature issues)
    @MockBean
    private PdfService pdfService;

    @Test
    void generateCertificate_endToEnd() throws Exception {

        // 1. Create customer
        Customer customer = new Customer();
        customer.setName("Acme Corp");
        customer.setApiKey("test-key");
        customer = customerRepo.save(customer);

        // 2. Create template
        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(customer);
        template.setName("Internship Certificate");
        template.setPlaceholders(List.of("name"));
        template.setHtmlTemplate("""
<!DOCTYPE html>
<html>
  <body>
    <h1>CERTIFICATE</h1>
    <p>Awarded to {{name}}</p>
  </body>
</html>
""");
        template = templateRepo.save(template);

        // 3. Mock PDF generation
        doAnswer(invocation -> {
            Certificate cert = invocation.getArgument(0);
            cert.setPdfPath("test.pdf");  // must exist or signing fails
            return null;
        }).when(pdfService).generatePdf(any());

        // 4. Call generate API
        mockMvc.perform(
                post("/api/certificates/generate")
                        .header("x-api-key", "test-key")
                        .param("templateId", template.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(mapper.writeValueAsString(
                                Map.of("data", Map.of("name", "John Doe"))
                        ))
        )
        .andExpect(status().isOk());
    }
}
