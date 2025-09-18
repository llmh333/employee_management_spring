package com.hit.employee_management_spring.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String JWT_SECRET_KEY;

    @Value("${jwt.expired.access.token}")
    private Integer EXPIRED_ACCESS_TOKEN;

    @Value("${jwt.expired.refresh.token}")
    private Integer EXPIRED_REFRESH_TOKEN;

    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes());
    }

    public String generateToken(UserPrincipal userPrincipal, boolean isRefreshToken) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userPrincipal.getAuthorities());
        claims.put("email", userPrincipal.getEmail());

        if (isRefreshToken) {
            return Jwts.builder()
                    .subject(userPrincipal.getUsername())
                    .issuedAt(new Date(new Date().getTime()))
                    .issuer("leminhi")
                    .expiration(new Date(System.currentTimeMillis() + EXPIRED_REFRESH_TOKEN * 60 * 1000L))
                    .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                    .claims(claims)
                    .compact();
        }

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(new Date().getTime()))
                .issuer("leminhi")
                .expiration(new Date(System.currentTimeMillis() + EXPIRED_ACCESS_TOKEN * 60 * 1000L))
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .claims(claims)
                .compact();
    }

    public String extractUsernameByToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public String extractEmailByToken(String token) {
        return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload().get("email").toString();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
