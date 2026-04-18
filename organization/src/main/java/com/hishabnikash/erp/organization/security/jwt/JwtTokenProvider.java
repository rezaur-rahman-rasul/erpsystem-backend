package com.hishabnikash.erp.organization.security.jwt;

import com.hishabnikash.erp.organization.config.properties.JwtProperties;
import com.hishabnikash.erp.organization.security.principal.CurrentUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtTokenProvider(@Value("${app.security.jwt.secret}") String secret,
                            JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtProperties = jwtProperties;
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .requireIssuer(jwtProperties.getIssuer())
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            throw ex;
        }
    }

    public CurrentUser buildCurrentUser(String token) {
        Claims claims = parseClaims(token);
        return new CurrentUser(
                claims.getSubject(),
                claims.get("username", String.class),
                claims.get("tenantId", String.class),
                extractAuthorities(claims)
        );
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Claims claims) {
        Object rawAuthorities = claims.get("authorities");
        if (rawAuthorities == null) {
            rawAuthorities = claims.get("permissions");
        }

        if (rawAuthorities instanceof Collection<?> values) {
            return values.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        if (rawAuthorities instanceof String value && !value.isBlank()) {
            return List.of(value.split(",")).stream()
                    .map(String::trim)
                    .filter(authority -> !authority.isBlank())
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }

        return List.of();
    }
}
