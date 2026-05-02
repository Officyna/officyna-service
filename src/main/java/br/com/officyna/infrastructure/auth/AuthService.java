package br.com.officyna.infrastructure.auth;

import br.com.officyna.administrative.user.domain.UserEntity;
import br.com.officyna.administrative.user.domain.service.UserService;
import br.com.officyna.administrative.user.repository.UserRepository;
import br.com.officyna.infrastructure.exception.NotFoundException;
import br.com.officyna.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private long expiration;

    public LoginResponse login(LoginRequest request) {
        String nomalizedEmail = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(nomalizedEmail, request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(nomalizedEmail);
        UserEntity entity = userRepository.findByEmail(nomalizedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado: " + nomalizedEmail));

        String token = jwtService.generateToken(userDetails);

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