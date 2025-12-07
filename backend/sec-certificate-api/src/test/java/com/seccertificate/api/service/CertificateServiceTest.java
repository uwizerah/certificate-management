package com.seccertificate.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seccertificate.api.domain.*;
import com.seccertificate.api.repository.CertificateRepository;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
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
    @Mock CertificateSignatureService signatureService;

    @InjectMocks
    CertificateService service;

    @Test
    void generate_shouldCreateCertificate() throws Exception {

        Customer customer = new Customer(); customer.setId(1L);

        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(customer);
        template.setPlaceholders(List.of("name"));

        Certificate saved = new Certificate(); saved.setId(10L);

        Certificate ready = new Certificate();
        ready.setId(10L);
        ready.setPdfPath("file.pdf");

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));
        when(mapper.writeValueAsString(any())).thenReturn("{\"name\":\"Bob\"}");
        when(certRepo.save(any())).thenReturn(saved);
        when(certRepo.findById(10L)).thenReturn(Optional.of(ready));

        doNothing().when(pdfService).generatePdf(any());
        doNothing().when(signatureService).sign(any());

        Certificate result = service.generate(customer, 1L, Map.of("name", "Bob"));

        assertNotNull(result.getPdfPath());
        verify(pdfService).generatePdf(saved);
        verify(signatureService).sign(ready);
        verify(certRepo, times(2)).save(any());   // ✅ FIX
    }


    // --------------------
    // ✅ UNAUTHORIZED TEMPLATE
    // --------------------
    @Test
    void generate_shouldRejectWrongCustomer() {
        Customer owner = new Customer(); owner.setId(1L);
        Customer attacker = new Customer(); attacker.setId(2L);

        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(owner);

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.generate(attacker, 1L, Map.of("name", "Bob")));

        assertEquals("Unauthorized template usage", ex.getMessage());
    }


    // --------------------
    // ✅ REQUIRED FIELD CHECK
    // --------------------
    @Test
    void generate_shouldFailIfRequiredFieldMissing() {

        Customer customer = new Customer(); customer.setId(1L);
        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(customer);
        template.setPlaceholders(List.of("name","course"));

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.generate(customer, 1L, Map.of("name","Bob")));

        assertTrue(ex.getMessage().contains("Missing required field"));
    }


    // --------------------
    // ✅ INFER ISSUED TO (MATCH)
    // --------------------
    @Test
    void generate_shouldInferIssuedTo_fromAlternativeKey() throws Exception {

        Customer customer = new Customer(); customer.setId(5L);

        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(customer);
        template.setPlaceholders(List.of("recipient"));

        Certificate saved = new Certificate(); saved.setId(1L);
        Certificate ready = new Certificate(); ready.setId(1L); ready.setPdfPath("x.pdf");

        when(templateRepo.findById(99L)).thenReturn(Optional.of(template));
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(certRepo.save(any())).thenReturn(saved);
        when(certRepo.findById(1L)).thenReturn(Optional.of(ready));

        doNothing().when(pdfService).generatePdf(any());
        doNothing().when(signatureService).sign(any());

        service.generate(customer, 99L, Map.of("recipient","ANN"));

        ArgumentCaptor<Certificate> captor = ArgumentCaptor.forClass(Certificate.class);

        // ✅ FIXED: save happens twice
        verify(certRepo, times(2)).save(captor.capture());

        Certificate created = captor.getAllValues().get(0); // FIRST SAVE = issuedTo logic

        assertEquals("ANN", created.getIssuedTo());
    }

    // --------------------
    // ✅ PDF NOT GENERATED
    // --------------------
    @Test
    void generate_shouldFailIfPdfPathNull() throws Exception {

        Customer c = new Customer(); c.setId(1L);
        CertificateTemplate t = new CertificateTemplate(); t.setCustomer(c); t.setPlaceholders(List.of("name"));

        Certificate saved = new Certificate(); saved.setId(1L);
        Certificate ready = new Certificate(); ready.setId(1L);

        when(templateRepo.findById(1L)).thenReturn(Optional.of(t));
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(certRepo.save(any())).thenReturn(saved);
        when(certRepo.findById(1L)).thenReturn(Optional.of(ready));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.generate(c, 1L, Map.of("name","A")));

        assertEquals("PDF not generated", ex.getMessage());
    }


    // --------------------
    // ✅ CERT NOT FOUND AFTER GENERATION
    // --------------------
    @Test
    void generate_shouldFailIfReloadMissing() throws Exception {

        Customer c = new Customer(); c.setId(2L);
        CertificateTemplate t = new CertificateTemplate(); t.setCustomer(c); t.setPlaceholders(List.of("name"));
        Certificate saved = new Certificate(); saved.setId(5L);

        when(templateRepo.findById(1L)).thenReturn(Optional.of(t));
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        when(certRepo.save(any())).thenReturn(saved);
        when(certRepo.findById(5L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.generate(c,1L,Map.of("name","A")));

        assertEquals("PDF generation failed", ex.getMessage());
    }


    // --------------------
    // ✅ DOWNLOAD SUCCESS
    // --------------------
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


    // --------------------
    // ✅ UNAUTHORIZED DOWNLOAD
    // --------------------
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


    // --------------------
    // ✅ PDF PATH NULL
    // --------------------
    @Test
    void downloadFor_shouldFailIfPdfNotGenerated() {

        Customer c = new Customer(); c.setId(1L);
        Certificate cert = new Certificate(); cert.setCustomer(c);

        when(certRepo.findById(1L)).thenReturn(Optional.of(cert));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.downloadFor(c,1L));

        assertEquals("PDF not yet generated", ex.getMessage());
    }


    // --------------------
    // ✅ FILE NOT FOUND
    // --------------------
    @Test
    void downloadFor_shouldFailWhenFileMissing() {

        Customer c = new Customer(); c.setId(1L);
        Certificate cert = new Certificate();
        cert.setCustomer(c);
        cert.setPdfPath("missing.pdf");

        when(certRepo.findById(1L)).thenReturn(Optional.of(cert));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.downloadFor(c,1L));

        assertEquals("PDF missing on server", ex.getMessage());
    }


    // --------------------
    // ✅ CERT NOT FOUND
    // --------------------
    @Test
    void downloadFor_shouldFailIfCertificateMissing() {

        when(certRepo.findById(9L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.downloadFor(new Customer(),9L));

        assertEquals("Certificate not found", ex.getMessage());
    }


    // --------------------
    // ✅ LIST
    // --------------------
    @Test
    void listFor_shouldReturnCertificates() {

        Customer c = new Customer(); c.setId(5L);

        when(certRepo.findByCustomerIdOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(new Certificate()));

        assertEquals(1, service.listFor(c).size());
    }
}
