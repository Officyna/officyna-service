package br.com.officyna.infrastructure.security;

import br.com.officyna.administrative.customer.domain.entity.Customer;
import br.com.officyna.administrative.customer.domain.repository.CustomerRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Quando token for gerado pela Lambda, deve buscar cliente na base de customer por email")
    void shouldAuthenticateCustomer_WhenTokenIsFromLambda_AndCustomerFoundByEmail() throws ServletException, IOException {
        String token = "lambda.jwt.token";
        String email = "contato@campanholi.com";
        Customer customer = Customer.builder()
                .id("c1")
                .name("Campanholi")
                .email(email)
                .document("12345678901")
                .active(true)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.isLambdaToken(token)).thenReturn(true);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));
        when(jwtService.isTokenValid(eq(token), any(UserDetails.class))).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        assertTrue(SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(a -> "ROLE_CUSTOMER".equals(a.getAuthority())));

        verify(customerRepository, times(1)).findByEmail(email);
        verifyNoInteractions(userDetailsService);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Quando token for gerado pela Lambda e email for nulo, deve buscar cliente por documento")
    void shouldAuthenticateCustomer_WhenTokenIsFromLambda_AndCustomerFoundByDocument() throws ServletException, IOException {
        String token = "lambda.jwt.token";
        String document = "12345678901";
        Customer customer = Customer.builder()
                .id("c1")
                .name("Campanholi")
                .document(document)
                .active(true)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(document);
        when(jwtService.isLambdaToken(token)).thenReturn(true);
        when(customerRepository.findByEmail(document)).thenReturn(Optional.empty());
        when(customerRepository.findByDocument(document)).thenReturn(Optional.of(customer));
        when(jwtService.isTokenValid(eq(token), any(UserDetails.class))).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(document, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(customerRepository, times(1)).findByEmail(document);
        verify(customerRepository, times(1)).findByDocument(document);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    @DisplayName("Quando token for gerado pela Lambda e cliente estiver inativo, não deve autenticar")
    void shouldNotAuthenticate_WhenCustomerInactive() throws ServletException, IOException {
        String token = "lambda.jwt.token";
        String email = "inativo@campanholi.com";
        Customer customer = Customer.builder()
                .id("c1")
                .name("Inativo")
                .email(email)
                .active(false)
                .build();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.isLambdaToken(token)).thenReturn(true);
        when(customerRepository.findByEmail(email)).thenReturn(Optional.of(customer));

        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Quando token não for da Lambda, deve buscar na base de usuários via userDetailsService")
    void shouldAuthenticateInternalUser_WhenTokenNotFromLambda() throws ServletException, IOException {
        String token = "internal.jwt.token";
        String email = "admin@officyna.com";
        UserDetails userDetails = new User(email, "pass", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(email);
        when(jwtService.isLambdaToken(token)).thenReturn(false);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(email, SecurityContextHolder.getContext().getAuthentication().getName());
        verify(userDetailsService, times(1)).loadUserByUsername(email);
        verifyNoInteractions(customerRepository);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve continuar a cadeia sem autenticar quando nao houver header Authorization")
    void shouldContinueChain_WhenNoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Deve continuar a cadeia sem autenticar quando header nao comecar com Bearer")
    void shouldContinueChain_WhenHeaderNotBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic 12345");

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    @DisplayName("Deve capturar JwtException e continuar a cadeia sem lancar erro 500")
    void shouldCatchJwtException_AndContinueChain() throws ServletException, IOException {
        String token = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenThrow(new JwtException("Token invalido ou expirado"));

        assertDoesNotThrow(() -> filter.doFilterInternal(request, response, filterChain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
