package br.com.officyna.infrastructure.auth;

import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.UserRole;
import br.com.officyna.administrative.user.repository.UserRepository;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private UserRepository userRepository;
    @Mock private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "expiration", 86_400_000L);
    }

    private UserEntity buildEntity(String email, UserRole role) {
        return UserEntity.builder()
                .id("user-1").name("João Silva").email(email)
                .password("encoded").userRole(role).active(true)
                .build();
    }

    private UserDetails buildUserDetails(String email) {
        return new User(email, "encoded", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token")
    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest("JOAO@EMAIL.COM", "senha123");
        String normalizedEmail = "joao@email.com";
        UserDetails userDetails = buildUserDetails(normalizedEmail);
        UserEntity entity = buildEntity(normalizedEmail, UserRole.ADMIN);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userDetailsService.loadUserByUsername(normalizedEmail)).thenReturn(userDetails);
        when(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(entity));
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-aqui");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token-aqui", response.token());
        assertEquals("Bearer", response.type());
        assertEquals("user-1", response.userId());
        assertEquals("João Silva", response.name());
        assertEquals(UserRole.ADMIN, response.role());
        assertEquals(86_400_000L, response.expiresIn());
    }

    @Test
    @DisplayName("Deve normalizar email para minúsculas antes de autenticar")
    void login_ShouldNormalizeEmail_BeforeAuthentication() {
        LoginRequest request = new LoginRequest("  JOAO@EMAIL.COM  ", "senha123");
        String normalizedEmail = "joao@email.com";
        UserDetails userDetails = buildUserDetails(normalizedEmail);

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername(normalizedEmail)).thenReturn(userDetails);
        when(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.of(buildEntity(normalizedEmail, UserRole.ADMIN)));
        when(jwtService.generateToken(userDetails)).thenReturn("token");

        authService.login(request);

        // Verifica que o authenticationManager foi chamado com email normalizado
        verify(authenticationManager).authenticate(
                argThat(auth -> auth instanceof UsernamePasswordAuthenticationToken &&
                        normalizedEmail.equals(((UsernamePasswordAuthenticationToken) auth).getPrincipal()))
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando credenciais são inválidas")
    void login_ShouldThrowException_WhenCredentialsAreInvalid() {
        LoginRequest request = new LoginRequest("joao@email.com", "senhaerrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException quando usuário não existe no repositório")
    void login_ShouldThrowNotFoundException_WhenUserNotFoundInRepository() {
        LoginRequest request = new LoginRequest("joao@email.com", "senha123");
        UserDetails userDetails = buildUserDetails("joao@email.com");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userDetailsService.loadUserByUsername("joao@email.com")).thenReturn(userDetails);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(any());
    }
}