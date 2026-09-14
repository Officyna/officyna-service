package br.com.officyna.infrastructure.config;

import br.com.officyna.infrastructure.security.JwtAuthenticationFilter;
import br.com.officyna.infrastructure.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    @DisplayName("Deve gerar e validar hash BCrypt com o encoder do SecurityConfig")
    void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);

        String rawPassword = "SenhaSegura@123456";
        String encoded = encoder.encode(rawPassword);

        assertTrue(encoder.matches(rawPassword, encoded));
    }
}
