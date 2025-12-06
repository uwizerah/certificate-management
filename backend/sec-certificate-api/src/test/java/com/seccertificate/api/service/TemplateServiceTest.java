package com.seccertificate.api.service;

import com.seccertificate.api.domain.CertificateTemplate;
import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.TemplateRequest;
import com.seccertificate.api.repository.CertificateTemplateRepository;
import com.seccertificate.api.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {

    @Mock
    CertificateTemplateRepository templateRepo;

    @Mock
    CustomerRepository customerRepo;

    @InjectMocks
    TemplateService service;

    // ---- createTemplate(Customer, TemplateRequest) ----

    @Test
    void createTemplate_shouldSaveTemplateWithCustomerAndPlaceholders() {
        Customer customer = new Customer();
        customer.setId(1L);

        TemplateRequest req = new TemplateRequest();
        req.setName("Cert");
        req.setHtmlTemplate("<h1>{{name}}</h1>");

        when(templateRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        CertificateTemplate result = service.createTemplate(customer, req);

        assertEquals("Cert", result.getName());
        assertEquals(customer, result.getCustomer());
        assertTrue(result.getPlaceholders().contains("name"));
        verify(templateRepo, times(1)).save(any());
    }

    // ---- createTemplate(Long, CertificateTemplate) ----

    @Test
    void createTemplateById_shouldAttachCustomerAndSave() {
        Customer customer = new Customer();
        customer.setId(10L);

        CertificateTemplate template = new CertificateTemplate();

        when(customerRepo.findById(10L)).thenReturn(Optional.of(customer));
        when(templateRepo.save(template)).thenReturn(template);

        CertificateTemplate result = service.createTemplate(10L, template);

        assertEquals(customer, result.getCustomer());
        verify(templateRepo).save(template);
    }

    @Test
    void createTemplateById_shouldThrow_whenCustomerMissing() {
        when(customerRepo.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.createTemplate(5L, new CertificateTemplate())
        );
    }

    // ---- getByCustomer ----

    @Test
    void getByCustomer_shouldReturnTemplates() {
        when(templateRepo.findByCustomerId(1L))
                .thenReturn(List.of(new CertificateTemplate()));

        var result = service.getByCustomer(1L);

        assertEquals(1, result.size());
        verify(templateRepo).findByCustomerId(1L);
    }

    // ---- getById ----

    @Test
    void getById_shouldReturnTemplate_whenOwnerMatches() {
        Customer customer = new Customer();
        customer.setId(1L);

        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(customer);

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));

        CertificateTemplate result = service.getById(customer, 1L);

        assertEquals(template, result);
    }

    @Test
    void getById_shouldThrow_whenNotOwner() {
        Customer owner = new Customer();
        owner.setId(1L);

        Customer attacker = new Customer();
        attacker.setId(2L);

        CertificateTemplate template = new CertificateTemplate();
        template.setCustomer(owner);

        when(templateRepo.findById(1L)).thenReturn(Optional.of(template));

        assertThrows(RuntimeException.class, () ->
                service.getById(attacker, 1L)
        );
    }

    @Test
    void getById_shouldThrow_whenNotFound() {
        when(templateRepo.findById(9L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.getById(new Customer(), 9L)
        );
    }
}
