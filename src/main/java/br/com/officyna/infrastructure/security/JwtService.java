package br.com.officyna.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class JwtService {


    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;


    public String generateToken(UserDetails userDetails) {

        log.info("Generating JWT token for user: {}", userDetails.getUsername());

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", getRole(userDetails))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();


        log.info("JWT token generated successfully for user: {}",
                userDetails.getUsername());

        return token;
    }


    private static @NonNull String getRole(UserDetails userDetails) {

        String roles = userDetails.getAuthorities()
                .stream()
                .map(a -> Objects.requireNonNull(a.getAuthority())
                        .replace("ROLE_", ""))
                .collect(Collectors.joining(", "));


        log.debug("User roles extracted: {}", roles);

        return roles;
    }


    public String extractUsername(String token) {

        log.debug("Extracting username from JWT token");

        String username = extractClaim(token, Claims::getSubject);

        log.info("Username extracted from token: {}", username);

        return username;
    }


    public boolean isTokenValid(String token, UserDetails userDetails) {

        log.debug("Validating JWT token for user: {}",
                userDetails.getUsername());


        String username = extractUsername(token);

        boolean valid = username.equals(userDetails.getUsername())
                && !isTokenExpired(token);


        if(valid){
            log.info("JWT token is valid for user: {}", username);
        } else {
            log.warn("Invalid JWT token for user: {}", username);
        }


        return valid;
    }


    private boolean isTokenExpired(String token) {

        Date expirationDate = extractClaim(token, Claims::getExpiration);

        boolean expired = expirationDate.before(new Date());


        if(expired){
            log.warn("JWT token expired at: {}", expirationDate);
        }

        return expired;
    }


    public Claims extractAllClaims(String token) {
        try {
            log.debug("Parsing JWT token claims");

            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver
    ) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isLambdaToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String issuer = claims.getIssuer();
            String type = claims.get("type", String.class);
            String roles = claims.get("roles", String.class);
            String customerId = claims.get("customerId", String.class);

            return "officyna-auth".equalsIgnoreCase(issuer)
                    || "CUSTOMER".equalsIgnoreCase(type)
                    || (roles != null && roles.contains("CUSTOMER"))
                    || customerId != null;
        } catch (Exception e) {
            log.warn("Error inspecting JWT token claims: {}", e.getMessage());
            return false;
        }
    }


    private SecretKey getSigningKey() {

        log.debug("Generating JWT signing key");


        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder()
                        .encodeToString(secret.getBytes())
        );


        log.debug("JWT signing key generated successfully");


        return Keys.hmacShaKeyFor(keyBytes);
    }
}