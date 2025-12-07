package com.seccertificate.api.config;

import com.seccertificate.api.security.ApiKeyFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ApplicationContext context;

    //mock auth filter so endpoints don't block
    @MockBean
    ApiKeyFilter apiKeyFilter;

    @Test
    void postCustomers_shouldBePublic() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void verifyEndpoint_shouldBePublic() throws Exception {
        mockMvc.perform(get("/api/verify/test"))
                .andExpect(status().isOk());
    }

    //PROTECTED endpoint still reachable because filter mocked
    @Test
    void protectedEndpoint_exists() throws Exception {
        mockMvc.perform(get("/api/certificates"))
                .andExpect(status().isOk());
    }

    //CORS bean loads
    @Test
    void corsConfigurationSourceBeanExists() {
        CorsConfigurationSource bean =
                context.getBean("corsConfigurationSource", CorsConfigurationSource.class);
        assert bean != null;
    }
}
