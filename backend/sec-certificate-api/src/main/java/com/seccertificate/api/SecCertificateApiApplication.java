package com.seccertificate.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class SecCertificateApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecCertificateApiApplication.class, args);
	}

}
