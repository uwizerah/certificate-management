package com.seccertificate.api.service;

import com.seccertificate.api.domain.Certificate;
import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.repository.CertificateRepository;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CertificateServiceTest {

    @Mock CertificateRepository certRepo;
    @Mock CertificateTemplateRepository templateRepo;
    @Mock PdfService pdfService;

    @InjectMocks
    CertificateService service;

    @Test
    void should_generate_certificate() throws Exception {
        Customer c = new Customer();
        c.setId(1L);

        CertificateTemplate t = new CertificateTemplate();
        t.setCustomer(c);

        Mockito.when(templateRepo.findById(2L)).thenReturn(Optional.of(t));
        Mockito.when(certRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Certificate cert = service.generate(c, 2L, Map.of("name", "Alice"));

        assertEquals("Alice", cert.getIssuedTo());
    }
}
