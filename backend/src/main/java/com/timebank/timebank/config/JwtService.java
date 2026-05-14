package com.timebank.timebank.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "app.jwt.secret boş; Render ortamında JWT_SECRET tanımlayın (HS256 için en az 32 karakter).");
        }
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret çok kısa ("
                            + secretBytes.length
                            + " bayt). HS256 için en az 32 bayt (32+ karakter ASCII) gerekir; Render’da JWT_SECRET’i uzatın.");
        }
        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String subjectEmail, String role) {
        Instant now = Instant.now();
        Instant exp = now.plus(expirationMinutes, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(subjectEmail)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token);
    }
}