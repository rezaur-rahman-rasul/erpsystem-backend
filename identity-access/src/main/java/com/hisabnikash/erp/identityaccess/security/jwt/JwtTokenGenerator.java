package com.hisabnikash.erp.identityaccess.security.jwt;

import com.hisabnikash.erp.identityaccess.config.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class JwtTokenGenerator {

    private final SecretKey secretKey;
    private final JwtProperties jwtProperties;

    public JwtTokenGenerator(@Value("${app.security.jwt.secret}") String secret,
                             JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtProperties = jwtProperties;
    }

    public String generateAccessToken(String userId,
                                      String username,
                                      String tenantId,
                                      Collection<String> authorities) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(jwtProperties.getAccessTokenExpirationMinutes(), ChronoUnit.MINUTES);
        Set<String> permissionSet = new LinkedHashSet<>(authorities);

        return Jwts.builder()
                .issuer(jwtProperties.getIssuer())
                .subject(userId)
                .claim("username", username)
                .claim("tenantId", tenantId)
                .claim("authorities", permissionSet)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }
}
