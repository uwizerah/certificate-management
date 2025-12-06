package com.seccertificate.api.service;

import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.repository.CertificateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    @Mock
    CertificateRepository repo;

    @InjectMocks
    PdfService service;

    @Test
    void generatePdf_shouldSetPathAndStatus_andSave() {
        // Arrange
        CertificateTemplate template = new CertificateTemplate();
        template.setHtmlTemplate("<html><body>{{name}}</body></html>");

        Certificate cert = new Certificate();
        cert.setId(1L);
        cert.setTemplate(template);
        cert.setDataJson("{\"name\":\"Alice\"}");

        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        service.generatePdf(cert);

        // Assert
        assertEquals("GENERATED", cert.getStatus());
        assertNotNull(cert.getPdfPath());
        verify(repo, times(1)).save(cert);
    }

    @Test
    void generatePdf_shouldThrowRuntimeException_whenTemplateIsNull() {
        // Arrange
        Certificate cert = new Certificate();
        cert.setId(2L);

        // Act + Assert
        assertThrows(RuntimeException.class, () -> service.generatePdf(cert));
    }
}
