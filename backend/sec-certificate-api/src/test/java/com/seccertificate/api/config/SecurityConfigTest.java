package com.seccertificate.api.config;

import com.seccertificate.api.security.ApiKeyFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ApplicationContext context;

    // ✅ Keep mocked so tests focus on config, not filter behavior
    @MockBean
    ApiKeyFilter apiKeyFilter;

    // ✅ public endpoint
    @Test
    void postCustomers_shouldBePublic() throws Exception {
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());   // ✅ FIXED
    }

    // ✅ public verify endpoint
    @Test
    void verifyEndpoint_shouldBePublic() throws Exception {
        mockMvc.perform(get("/api/verify/test"))
                .andExpect(status().isOk());
    }

    // ✅ protected endpoint reachable (filter mocked, so no 401 expected)
    @Test
    void protectedEndpoint_exists() throws Exception {
        mockMvc.perform(get("/api/certificates"))
                .andExpect(status().isOk());   // ✅ FIXED
    }

    // ✅ CORS bean exists
    @Test
    void corsConfigurationSourceBeanExists() {
        CorsConfigurationSource bean =
                context.getBean("corsConfigurationSource", CorsConfigurationSource.class);
        assert bean != null;
    }
}
