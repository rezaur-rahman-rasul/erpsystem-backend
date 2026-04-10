package com.hisabnikash.erp.gateway.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return Mono.just(new JwtAuthenticationToken(
                jwt,
                extractAuthorities(jwt),
                jwt.getClaimAsString("username")
        ));
    }

    private Collection<? extends GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object rawAuthorities = jwt.getClaims().get("authorities");
        if (rawAuthorities == null) {
            rawAuthorities = jwt.getClaims().get("permissions");
        }

        if (rawAuthorities instanceof Collection<?> collection) {
            return collection.stream()
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
