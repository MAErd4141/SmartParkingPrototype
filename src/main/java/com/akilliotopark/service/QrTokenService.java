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

@Service
public class QrTokenService {

    @Value("${app.qr.secret}")
    private String secret; // En az 32 byte olmalı

    @Value("${app.qr.ttl-minutes:30}")
    private long ttlMinutes;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** QR Token oluşturur (JWT) — ChronoUnit kullanılmıyor */
    public String generateToken(Long reservationId, String plateNumber) {
        Instant now = Instant.now();
        // ttlMinutes'i saniyeye çevir (overflow korumalı)
        long ttlSeconds = Math.multiplyExact(ttlMinutes, 60L);
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .setSubject("ReservationQR")
                .claim("rid", reservationId)
                .claim("plate", plateNumber)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** Token’ı ve plakayı doğrular */
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

        } catch (JwtException e) {
            return false; // geçersiz/expired token
        }
    }
}
