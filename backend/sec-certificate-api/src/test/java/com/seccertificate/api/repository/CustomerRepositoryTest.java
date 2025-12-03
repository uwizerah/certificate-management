package com.seccertificate.api.repository;

import com.seccertificate.api.domain.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByApiKey_ShouldReturnCustomer_WhenKeyExists() {
        // Arrange
        Customer customer = new Customer();
        String apiKey = UUID.randomUUID().toString();
        customer.setApiKey(apiKey);
        customer.setName("API Key Test Customer");
        
        entityManager.persist(customer);
        entityManager.flush();

        // Act
        Optional<Customer> found = customerRepository.findByApiKey(apiKey);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("API Key Test Customer");
    }

    @Test
    void findByApiKey_ShouldReturnEmpty_WhenKeyDoesNotExist() {
        // Arrange
        String nonExistentKey = "non-existent-key";

        // Act
        Optional<Customer> found = customerRepository.findByApiKey(nonExistentKey);

        // Assert
        assertThat(found).isNotPresent();
    }
}