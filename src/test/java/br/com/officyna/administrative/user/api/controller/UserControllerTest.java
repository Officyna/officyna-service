package br.com.officyna.administrative.user.api.controller;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.UserRole;
import br.com.officyna.administrative.user.domain.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ─────────────── GET /api/users ───────────────

    @Test
    @DisplayName("Deve retornar lista de todos os usuários com status 200")
    void findAll_ShouldReturnOk() throws Exception {
        when(userService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há usuários")
    void findAll_ShouldReturnEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(userService, times(1)).findAll();
    }

    // ─────────────── GET /api/users/{id} ───────────────

    @Test
    @DisplayName("Deve retornar usuário por ID com status 200")
    void findById_ShouldReturnOk() throws Exception {
        String id = "usr-1";
        when(userService.findById(id)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk());

        verify(userService, times(1)).findById(id);
    }

    // ─────────────── GET /api/users/email/{email} ───────────────

    @Test
    @DisplayName("Deve retornar usuário por email com status 200")
    void findByEmail_ShouldReturnOk() throws Exception {
        String email = "joao@email.com";
        when(userService.findByEmail(email)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/users/email/{email}", email))
                .andExpect(status().isOk());

        verify(userService, times(1)).findByEmail(email);
    }

    // ─────────────── POST /api/users ───────────────

    @Test
    @DisplayName("Deve criar usuário e retornar status 201")
    void create_ShouldReturnCreated() throws Exception {
        when(userService.create(any(UserRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated());

        verify(userService, times(1)).create(any(UserRequest.class));
    }

    // ─────────────── PUT /api/users/{id} ───────────────

    @Test
    @DisplayName("Deve atualizar usuário e retornar status 200")
    void update_ShouldReturnOk() throws Exception {
        String id = "usr-1";
        when(userService.update(eq(id), any(UserRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());

        verify(userService, times(1)).update(eq(id), any(UserRequest.class));
    }

    // ─────────────── DELETE /api/users/{id} ───────────────

    @Test
    @DisplayName("Deve desativar usuário e retornar status 204")
    void delete_ShouldReturnNoContent() throws Exception {
        String id = "usr-1";
        doNothing().when(userService).delete(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(id);
    }

    // ─────────────── helpers ───────────────

    private UserRequest buildRequest() {
        return new UserRequest("João Silva", "joao@email.com", "Senha123", UserRole.ATTENDANT);
    }

    private UserResponse buildResponse() {
        return UserResponse.builder()
                .id("usr-1")
                .name("João Silva")
                .email("joao@email.com")
                .userRole(UserRole.ATTENDANT)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
}