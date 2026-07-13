package br.com.officyna.infrastructure.auth;

import br.com.officyna.administrative.user.domain.entity.User;
import br.com.officyna.administrative.user.domain.repository.UserRepository;
import br.com.officyna.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long expiration;

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        User entity = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalStateException("Estado inconsistente: usuário autenticado sem registro"));

        String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());

        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(expiration)
                .userId(entity.getId())
                .name(entity.getName())
                .role(entity.getUserRole())
                .build();
    }

    private static @NonNull String normalizeEmail(String email) {
        return email.toLowerCase(Locale.ROOT).trim();
    }
}