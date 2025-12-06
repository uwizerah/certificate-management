package com.seccertificate.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seccertificate.api.domain.*;
import com.seccertificate.api.repository.CertificateRepository;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock CertificateRepository certRepo;
    @Mock CertificateTemplateRepository templateRepo;
    @Mock PdfService pdfService;
    @Mock ObjectMapper mapper;

    @InjectMocks
    CertificateService service;

    @Test
    void generate_shouldCreateCertificate() throws Exception {
        Customer customer = new Customer(); customer.setId(1L);
        CertificateTemplate template = new CertificateTemplate(); template.setCustomer(customer);

        Certificate saved = new Certificate(); saved.setId(10L);
        Certificate ready = new Certificate(); ready.setId(10L); ready.setPdfPath("file.pdf");

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(certRepo.save(any())).thenReturn(saved);
        when(certRepo.findById(10L)).thenReturn(Optional.of(ready));

        Certificate result = service.generate(customer, 1L, Map.of("name", "Bob"));

        assertNotNull(result.getPdfPath());
        verify(pdfService).generatePdf(saved);
    }

    @Test
    void generate_shouldRejectWrongCustomer() {
        Customer owner = new Customer(); owner.setId(1L);
        Customer attacker = new Customer(); attacker.setId(2L);

        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(owner);

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.generate(attacker, 1L, Map.of()));

        assertEquals("Unauthorized template usage", ex.getMessage());
    }

    @Test
    void downloadFor_shouldReturnPdf() throws Exception {
        Customer c = new Customer(); c.setId(1L);
        Path temp = Files.createTempFile("cert", ".pdf");

        Certificate cert = new Certificate();
        cert.setCustomer(c);
        cert.setPdfPath(temp.toString());

        when(certRepo.findById(1L)).thenReturn(Optional.of(cert));

        Resource r = service.downloadFor(c, 1L);

        assertTrue(r.exists());
    }

    @Test
    void downloadFor_shouldBlockOtherUser() {
        Customer owner = new Customer(); owner.setId(1L);
        Customer other = new Customer(); other.setId(2L);

        Certificate cert = new Certificate();
        cert.setCustomer(owner);
        cert.setPdfPath("x.pdf");

        when(certRepo.findById(1L)).thenReturn(Optional.of(cert));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.downloadFor(other, 1L));

        assertEquals("Unauthorized certificate access", ex.getMessage());
    }

    @Test
    void listFor_shouldReturnCertificates() {
        Customer c = new Customer(); c.setId(5L);

        when(certRepo.findByCustomerIdOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(new Certificate()));

        assertEquals(1, service.listFor(c).size());
    }
}
