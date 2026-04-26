package br.com.officyna.administrative.user.domain.service;

import br.com.officyna.administrative.user.api.resources.UserRequest;
import br.com.officyna.administrative.user.api.resources.UserResponse;
import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.UserRole;
import br.com.officyna.administrative.user.domain.mapper.UserMapper;
import br.com.officyna.administrative.user.repository.UserRepository;
import br.com.officyna.infrastructure.exception.DomainException;
import br.com.officyna.infrastructure.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private UserEntity buildEntity(String id, String email, UserRole role, boolean active) {
        return UserEntity.builder()
                .id(id).name("João Silva").email(email)
                .password("encoded").userRole(role).active(active)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    private UserRequest buildRequest(String email) {
        return new UserRequest("João Silva", email, "senha123", UserRole.ATTENDANT);
    }

    private UserResponse buildResponse(String id, String email) {
        return UserResponse.builder()
                .id(id).name("João Silva").email(email)
                .userRole(UserRole.ATTENDANT).active(true)
                .createdAt(LocalDateTime.now()).build();
    }

    private void setupSecurityContext(String role) {
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenAnswer(i -> List.of(new SimpleGrantedAuthority(role)));
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    // ─── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar todos os usuários ativos")
    void findAll_ShouldReturnActiveUsers() {
        UserEntity e1 = buildEntity("1", "a@email.com", UserRole.ADMIN, true);
        UserEntity e2 = buildEntity("2", "b@email.com", UserRole.MECHANIC, true);
        UserResponse r1 = buildResponse("1", "a@email.com");
        UserResponse r2 = buildResponse("2", "b@email.com");

        when(userRepository.findByActiveTrue()).thenReturn(List.of(e1, e2));
        when(userMapper.toResponse(e1)).thenReturn(r1);
        when(userMapper.toResponse(e2)).thenReturn(r2);

        List<UserResponse> result = userService.findAll();

        assertEquals(2, result.size());
        verify(userRepository).findByActiveTrue();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar usuário pelo ID")
    void findById_ShouldReturnUser() {
        UserEntity entity = buildEntity("1", "a@email.com", UserRole.ADMIN, true);
        UserResponse response = buildResponse("1", "a@email.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.findById("1");

        assertEquals("1", result.getId());
        verify(userRepository).findById("1");
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existe pelo ID")
    void findById_ShouldThrowNotFoundException() {
        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById("inexistente"));
    }

    // ─── findByEmail ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar usuário pelo email")
    void findByEmail_ShouldReturnUser() {
        UserEntity entity = buildEntity("1", "joao@email.com", UserRole.ADMIN, true);
        UserResponse response = buildResponse("1", "joao@email.com");

        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(entity));
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.findByEmail("joao@email.com");

        assertEquals("joao@email.com", result.getEmail());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando email não existe")
    void findByEmail_ShouldThrowNotFoundException() {
        when(userRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail("naoexiste@email.com"));
    }

    // ─── create ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("ADMIN deve criar usuário com sucesso")
    void create_ShouldCreateUser_WhenCalledByAdmin() {
        setupSecurityContext("ROLE_ADMIN");
        UserRequest request = buildRequest("novo@email.com");
        UserEntity entity = buildEntity(null, "novo@email.com", UserRole.ATTENDANT, true);
        UserEntity saved = buildEntity("newId", "novo@email.com", UserRole.ATTENDANT, true);
        UserResponse response = buildResponse("newId", "novo@email.com");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals("newId", result.getId());
        verify(passwordEncoder).encode(request.password());
        verify(userRepository).save(entity);
    }

    @Test
    @DisplayName("MANAGER deve criar usuário com sucesso")
    void create_ShouldCreateUser_WhenCalledByManager() {
        setupSecurityContext("ROLE_MANAGER");
        UserRequest request = buildRequest("novo@email.com");
        UserEntity entity = buildEntity(null, "novo@email.com", UserRole.ATTENDANT, true);
        UserEntity saved = buildEntity("newId", "novo@email.com", UserRole.ATTENDANT, true);
        UserResponse response = buildResponse("newId", "novo@email.com");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Deve lançar DomainException quando usuário sem permissão tenta criar")
    void create_ShouldThrowDomainException_WhenCalledByAttendant() {
        setupSecurityContext("ROLE_ATTENDANT");
        UserRequest request = buildRequest("novo@email.com");

        assertThrows(DomainException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar DomainException quando email já está em uso por usuário ativo")
    void create_ShouldThrowDomainException_WhenEmailAlreadyActive() {
        setupSecurityContext("ROLE_ADMIN");
        UserEntity existing = buildEntity("1", "existente@email.com", UserRole.ATTENDANT, true);
        UserRequest request = buildRequest("existente@email.com");
        when(userRepository.findByEmail("existente@email.com")).thenReturn(Optional.of(existing));

        assertThrows(DomainException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve reutilizar ID de usuário inativo ao criar com mesmo email")
    void create_ShouldReuseId_WhenEmailBelongsToInactiveUser() {
        setupSecurityContext("ROLE_ADMIN");
        UserEntity inactive = buildEntity("oldId", "reuso@email.com", UserRole.ATTENDANT, false);
        UserRequest request = buildRequest("reuso@email.com");
        UserEntity entity = buildEntity(null, "reuso@email.com", UserRole.ATTENDANT, true);
        UserEntity saved = buildEntity("oldId", "reuso@email.com", UserRole.ATTENDANT, true);
        UserResponse response = buildResponse("oldId", "reuso@email.com");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(inactive));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded");
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals("oldId", result.getId());
        assertEquals("oldId", entity.getId()); // ID foi reutilizado
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve atualizar usuário existente com sucesso")
    void update_ShouldUpdateUser() {
        UserEntity entity = buildEntity("1", "antigo@email.com", UserRole.ATTENDANT, true);
        UserRequest request = buildRequest("novo@email.com");
        UserResponse response = buildResponse("1", "novo@email.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(entity));
        when(userRepository.existsByEmail("novo@email.com")).thenReturn(false);
        doNothing().when(userMapper).updateEntity(entity, request);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        UserResponse result = userService.update("1", request);

        assertNotNull(result);
        verify(userMapper).updateEntity(entity, request);
    }

    @Test
    @DisplayName("Deve lançar DomainException ao atualizar com email já em uso")
    void update_ShouldThrowDomainException_WhenEmailTaken() {
        UserEntity entity = buildEntity("1", "antigo@email.com", UserRole.ATTENDANT, true);
        UserRequest request = buildRequest("ocupado@email.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(entity));
        when(userRepository.existsByEmail("ocupado@email.com")).thenReturn(true);

        assertThrows(DomainException.class, () -> userService.update("1", request));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve verificar duplicidade ao manter o mesmo email")
    void update_ShouldNotCheckDuplicate_WhenEmailUnchanged() {
        UserEntity entity = buildEntity("1", "mesmo@email.com", UserRole.ATTENDANT, true);
        UserRequest request = buildRequest("mesmo@email.com");
        UserResponse response = buildResponse("1", "mesmo@email.com");

        when(userRepository.findById("1")).thenReturn(Optional.of(entity));
        doNothing().when(userMapper).updateEntity(entity, request);
        when(userRepository.save(entity)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(response);

        userService.update("1", request);

        verify(userRepository, never()).existsByEmail(any());
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve desativar usuário ao invés de deletar fisicamente")
    void delete_ShouldDeactivateUser() {
        UserEntity entity = buildEntity("1", "joao@email.com", UserRole.ATTENDANT, true);
        when(userRepository.findById("1")).thenReturn(Optional.of(entity));

        userService.delete("1");

        assertFalse(entity.getActive());
        verify(userRepository).save(entity);
    }

    @Test
    @DisplayName("Deve lançar NotFoundException ao deletar usuário inexistente")
    void delete_ShouldThrowNotFoundException() {
        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.delete("inexistente"));
        verify(userRepository, never()).save(any());
    }
}