package com.club.git.backend_app.security;

import com.club.git.backend_app.entity.Membre;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    // ── Génération ───────────────────────────────────────────────────────────

    public String generateAccessToken(Membre membre) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", membre.getRole().name());
        claims.put("nom", membre.getNom());
        claims.put("prenom", membre.getPrenom());

        return Jwts.builder()
                .claims(claims)
                .subject(membre.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ── Extraction ───────────────────────────────────────────────────────────

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // ── Validation ───────────────────────────────────────────────────────────

    public boolean isTokenValid(String token, Membre membre) {
        try {
            final String email = extractEmail(token);
            return email.equals(membre.getEmail()) && !isTokenExpired(token);
        } catch (JwtException e) {
            log.warn("Token JWT invalide : {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token expiré : {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("Token malformé : {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("Signature invalide : {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token vide ou null : {}", e.getMessage());
        }
        return false;
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
