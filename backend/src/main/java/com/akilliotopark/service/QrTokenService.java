package com.akilliotopark.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class QrTokenService {

    @Value("${app.qr.secret}")
    private String secret;

    @Value("${app.qr.ttl-minutes:30}")
    private long ttlMinutes;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID reservationId, String plateNumber) {
        Instant now = Instant.now();
        long ttlSeconds = Math.multiplyExact(ttlMinutes, 60L);
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .setSubject(reservationId.toString())
                .claim("plate", plateNumber)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token, String plateNumber) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String plateFromToken = claims.get("plate", String.class);
            Date exp = claims.getExpiration();

            return plateFromToken != null
                    && plateNumber != null
                    && plateFromToken.equalsIgnoreCase(plateNumber)
                    && exp.after(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}