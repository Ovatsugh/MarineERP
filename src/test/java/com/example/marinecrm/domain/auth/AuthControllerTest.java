package com.example.marinecrm.domain.auth;

import com.example.marinecrm.domain.auth.DTO.AuthRequest;
import com.example.marinecrm.domain.auth.DTO.AuthResponse;
import com.example.marinecrm.domain.auth.DTO.LoginRequest;
import com.example.marinecrm.domain.auth.service.LoginService;
import com.example.marinecrm.domain.auth.service.RegisterService;
import com.example.marinecrm.exceptions.InvalidCredentialsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock private LoginService loginService;
    @Mock private RegisterService registerService;

    @InjectMocks private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void login_shouldReturn200WithToken() throws Exception {
        LoginRequest request = new LoginRequest("admin@teste.com", "senha123");
        when(loginService.execute(any())).thenReturn(new AuthResponse("jwt.token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token"));
    }

    @Test
    void login_shouldReturn401WhenInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin@teste.com", "senha_errada");
        when(loginService.execute(any())).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_shouldReturn200WithToken() throws Exception {
        AuthRequest request = new AuthRequest("João", "joao@teste.com", "senha123", UUID.randomUUID());
        when(registerService.execute(any())).thenReturn(new AuthResponse("jwt.token.novo"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.novo"));
    }

    @Test
    void login_shouldReturn400WhenBodyIsInvalid() throws Exception {
        String invalidBody = """
                { "email": "admin@teste.com" }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest());
    }
}
