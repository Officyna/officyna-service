package br.com.officyna.infrastructure.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    // Secret longo o suficiente para HMAC-SHA256
    private static final String TEST_SECRET =
            "officyna-test-secret-key-para-hmac-deve-ter-256bits-minimo-aqui";
    private static final long TEST_EXPIRATION = 3_600_000L; // 1 hora

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", TEST_EXPIRATION);
    }

    private UserDetails buildUserDetails(String email, String role) {
        return new User(email, "senha", List.of(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    // ─── generateToken / extractUsername ─────────────────────────────────────

    @Test
    @DisplayName("Deve gerar token com o email como subject")
    void generateToken_ShouldSetEmailAsSubject() {
        UserDetails user = buildUserDetails("joao@email.com", "ADMIN");

        String token = jwtService.generateToken(user);
        String extracted = jwtService.extractUsername(token);

        assertEquals("joao@email.com", extracted);
    }

    @Test
    @DisplayName("Deve gerar tokens diferentes para usuários diferentes")
    void generateToken_ShouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtService.generateToken(buildUserDetails("a@email.com", "ADMIN"));
        String token2 = jwtService.generateToken(buildUserDetails("b@email.com", "MECHANIC"));

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Deve gerar token não nulo e não vazio")
    void generateToken_ShouldReturnNonBlankToken() {
        String token = jwtService.generateToken(buildUserDetails("joao@email.com", "ADMIN"));

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    // ─── isTokenValid ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Token válido deve ser aceito para o usuário correto")
    void isTokenValid_ShouldReturnTrue_ForMatchingUser() {
        UserDetails user = buildUserDetails("joao@email.com", "ADMIN");
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    @DisplayName("Token deve ser inválido para usuário diferente do subject")
    void isTokenValid_ShouldReturnFalse_ForDifferentUser() {
        UserDetails owner = buildUserDetails("dono@email.com", "ADMIN");
        UserDetails other = buildUserDetails("outro@email.com", "ADMIN");
        String token = jwtService.generateToken(owner);

        assertFalse(jwtService.isTokenValid(token, other));
    }

    @Test
    @DisplayName("Token expirado deve lançar ExpiredJwtException ao tentar validar")
    void isTokenValid_ShouldThrowExpiredJwtException_ForExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L); // já expirado
        UserDetails user = buildUserDetails("joao@email.com", "ADMIN");
        String expiredToken = jwtService.generateToken(user);

        ReflectionTestUtils.setField(jwtService, "expiration", TEST_EXPIRATION); // restaura

        // jjwt lança ExpiredJwtException ao parsear — não retorna false
        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, user));
    }

    // ─── extractUsername ──────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve extrair corretamente o username de tokens com roles diferentes")
    void extractUsername_ShouldWorkForAllRoles() {
        for (String role : List.of("ADMIN", "MANAGER", "ATTENDANT", "MECHANIC")) {
            UserDetails user = buildUserDetails("user@email.com", role);
            String token = jwtService.generateToken(user);
            assertEquals("user@email.com", jwtService.extractUsername(token));
        }
    }
}