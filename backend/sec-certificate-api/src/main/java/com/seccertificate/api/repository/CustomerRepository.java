package com.seccertificate.api.repository;

import com.seccertificate.api.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByApiKey(String apiKey);
}
