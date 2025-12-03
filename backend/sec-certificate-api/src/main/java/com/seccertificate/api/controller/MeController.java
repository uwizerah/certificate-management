package com.seccertificate.api.controller;

import com.seccertificate.api.domain.Customer;
import com.seccertificate.api.dto.MeResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MeController {

    @GetMapping("/me")
    public MeResponse me(@AuthenticationPrincipal Customer customer) {
        return new MeResponse(customer.getName());
    }
}
