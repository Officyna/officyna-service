package br.com.officyna.infrastructure.security;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        logger.info("Processing authentication for ");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Request without Bearer token.");
            filterChain.doFilter(request, response);
            return;
        }
        logger.debug("JWT token found in request header.");

        String token = authHeader.substring(7);

        try {
            String identifier = jwtService.extractUsername(token);

            if (identifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails;

                if (jwtService.isLambdaToken(token)) {
                    logger.debug("Token gerado pela Lambda. Buscando cliente por email/documento: {}", identifier);
                    Customer customer = customerRepository.findByEmail(identifier)
                            .or(() -> customerRepository.findByDocument(identifier))
                            .orElseThrow(() -> new UsernameNotFoundException("Cliente não encontrado: " + identifier));

                    if (Boolean.FALSE.equals(customer.getActive())) {
                        throw new UsernameNotFoundException("Cliente inativo no sistema: " + identifier);
                    }

                    String username = customer.getEmail() != null && !customer.getEmail().isBlank()
                            ? customer.getEmail()
                            : customer.getDocument();

                    userDetails = new User(
                            username,
                            "",
                            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
                    );
                } else {
                    logger.debug("Token de usuário interno. Buscando na base de usuários: {}", identifier);
                    userDetails = userDetailsService.loadUserByUsername(identifier);
                }

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (UsernameNotFoundException | JwtException e) {
            logger.warn("Falha na autenticação JWT: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erro inesperado durante processamento do token JWT: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}