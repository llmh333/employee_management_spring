package com.hit.employee_management_spring.security;

import com.hit.employee_management_spring.constant.ErrorMessage;
import com.hit.employee_management_spring.constant.TypeToken;
import com.hit.employee_management_spring.exception.BadRequestException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

        String authorities = userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(","));
        claims.put("authorities", authorities);
        claims.put("email", userPrincipal.getEmail());
        claims.put("type_token", isRefreshToken ? TypeToken.REFRESH_TOKEN : TypeToken.ACCESS_TOKEN);

        if (isRefreshToken) {
            return Jwts.builder()
                    .subject(userPrincipal.getUsername())
                    .issuedAt(new Date(new Date().getTime()))
                    .issuer("leminhi")
                    .expiration(new Date(System.currentTimeMillis() + EXPIRED_REFRESH_TOKEN * 24 * 60 * 1000L))
                    .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                    .claims(claims)
                    .compact();
        }

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(new Date().getTime()))
                .issuer("leminhi")
                .expiration(new Date(System.currentTimeMillis() + EXPIRED_ACCESS_TOKEN * 24 * 60 * 1000L))
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
        }
        catch (Exception e) {
            throw new BadRequestException(ErrorMessage.Auth.UNAUTHENTICATED);
        }
    }
}
