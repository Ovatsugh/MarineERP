package com.example.marinecrm.domain.user;

import com.example.marinecrm.domain.user.DTO.UserRequest;
import com.example.marinecrm.domain.user.DTO.UserResponse;
import com.example.marinecrm.domain.user.DTO.UserUpdateRequest;
import com.example.marinecrm.domain.user.Services.*;
import com.example.marinecrm.enums.Roles;
import com.example.marinecrm.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.SpringDataJacksonConfiguration;
import org.springframework.data.web.config.SpringDataWebSettings;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock private CreateUserService createUserService;
    @Mock private DeleteUserService deleteUserService;
    @Mock private GetAllUserService getAllUserService;
    @Mock private GetUserByIdService getUserByIdService;
    @Mock private UpdateUserService updateUserService;
    @Mock private GetMeService getMeService;

    @InjectMocks private UserController userController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new SpringDataJacksonConfiguration.PageModule(
                    new SpringDataWebSettings(EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)));

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void getMe_shouldReturn200() throws Exception {
        UserResponse response = new UserResponse(UUID.randomUUID(), "Admin", "admin@teste.com", Roles.ADMIN);
        when(getMeService.execute(null)).thenReturn(response);

        mockMvc.perform(get("/admin/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin"));
    }

    @Test
    void createUser_shouldReturn200() throws Exception {
        UserRequest request = new UserRequest("Funcionário", "func@teste.com", "senha123", Roles.EMPLOYEE);
        UserResponse response = new UserResponse(UUID.randomUUID(), "Funcionário", "func@teste.com", Roles.EMPLOYEE);

        when(createUserService.execute(any())).thenReturn(response);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Funcionário"));
    }

    @Test
    void getUsers_shouldReturn200WithPage() throws Exception {
        UserResponse user = new UserResponse(UUID.randomUUID(), "Admin", "admin@teste.com", Roles.ADMIN);
        when(getAllUserService.execute(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(user)));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Admin"));
    }

    @Test
    void getUserById_shouldReturn200WhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        UserResponse response = new UserResponse(id, "Func", "func@teste.com", Roles.EMPLOYEE);
        when(getUserByIdService.execute(id)).thenReturn(response);

        mockMvc.perform(get("/admin/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Func"));
    }

    @Test
    void getUserById_shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(getUserByIdService.execute(id)).thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/admin/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/admin/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUser_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(id, "Func Novo", "funcnovo@teste.com", null, Roles.EMPLOYEE);
        UserResponse response = new UserResponse(id, "Func Novo", "funcnovo@teste.com", Roles.EMPLOYEE);

        when(updateUserService.execute(any())).thenReturn(response);

        mockMvc.perform(put("/admin/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Func Novo"));
    }
}
