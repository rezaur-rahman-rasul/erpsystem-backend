package com.hisabnikash.erp.gateway.security;

import com.hisabnikash.erp.gateway.config.properties.GatewayPolicyProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TenantPolicyFilter implements GlobalFilter, Ordered {

    private final GatewayPolicyProperties policyProperties;

    public TenantPolicyFilter(GatewayPolicyProperties policyProperties) {
        this.policyProperties = policyProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (!isTenantAwarePath(path)) {
            return chain.filter(exchange);
        }

        return exchange.getPrincipal()
                .filter(Authentication.class::isInstance)
                .cast(Authentication.class)
                .map(Authentication::getPrincipal)
                .filter(Jwt.class::isInstance)
                .cast(Jwt.class)
                .flatMap(jwt -> {
                    String tenantId = jwt.getClaimAsString("tenantId");
                    if (tenantId == null || tenantId.isBlank()) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -20;
    }

    private boolean isTenantAwarePath(String path) {
        return policyProperties.getTenantAwarePrefixes().stream().anyMatch(path::startsWith);
    }
}
