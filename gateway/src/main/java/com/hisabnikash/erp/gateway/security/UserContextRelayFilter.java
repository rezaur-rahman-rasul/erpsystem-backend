package com.hisabnikash.erp.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserContextRelayFilter implements GlobalFilter, Ordered {

    private static final Pattern API_VERSION_PATTERN = Pattern.compile("/api/(v\\d+)(?:/|$)");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                .filter(Authentication.class::isInstance)
                .cast(Authentication.class)
                .map(authentication -> mutateExchange(exchange, authentication))
                .defaultIfEmpty(exchange)
                .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -10;
    }

    private ServerWebExchange mutateExchange(ServerWebExchange exchange, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            return exchange;
        }

        ServerHttpRequest request = exchange.getRequest().mutate()
                .header("X-User-Id", jwt.getSubject())
                .header("X-Username", jwt.getClaimAsString("username"))
                .header("X-Tenant-Id", jwt.getClaimAsString("tenantId"))
                .headers(headers -> resolveApiVersion(exchange.getRequest().getPath().value(), headers))
                .build();

        return exchange.mutate().request(request).build();
    }

    private void resolveApiVersion(String path, org.springframework.http.HttpHeaders headers) {
        Matcher matcher = API_VERSION_PATTERN.matcher(path);
        if (matcher.find()) {
            headers.set("X-Api-Version", matcher.group(1));
        }
    }
}
