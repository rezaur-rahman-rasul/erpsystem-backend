package com.hisabnikash.erp.gateway.security;

import com.hisabnikash.erp.gateway.config.properties.GatewayPolicyProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RequestThrottlingFilter implements GlobalFilter, Ordered {

    private final GatewayPolicyProperties policyProperties;
    private final Map<String, FixedWindowCounter> counters = new ConcurrentHashMap<>();

    public RequestThrottlingFilter(GatewayPolicyProperties policyProperties) {
        this.policyProperties = policyProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!policyProperties.getThrottling().isEnabled() || isExemptPath(exchange.getRequest().getPath().value())) {
            return chain.filter(exchange);
        }

        String key = resolveClientKey(exchange) + ":" + resolveServiceKey(exchange.getRequest().getPath().value());
        FixedWindowCounter counter = counters.computeIfAbsent(key, ignored -> new FixedWindowCounter());
        if (!counter.tryAcquire(policyProperties.getThrottling().getRequestsPerMinute())) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            exchange.getResponse().getHeaders().add("Retry-After", "60");
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -30;
    }

    private boolean isExemptPath(String path) {
        return path.startsWith("/actuator")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/api/v1/routes")
                || path.startsWith("/identity/api/v1/auth");
    }

    private String resolveClientKey(ServerWebExchange exchange) {
        InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
        return remoteAddress == null ? "unknown-client" : remoteAddress.getAddress().getHostAddress();
    }

    private String resolveServiceKey(String path) {
        String[] segments = path.split("/");
        return segments.length > 1 ? segments[1] : "root";
    }

    private static final class FixedWindowCounter {
        private long windowStartEpochSecond = Instant.now().getEpochSecond();
        private int count;

        synchronized boolean tryAcquire(int limit) {
            long now = Instant.now().getEpochSecond();
            if (now - windowStartEpochSecond >= 60) {
                windowStartEpochSecond = now;
                count = 0;
            }
            if (count >= limit) {
                return false;
            }
            count++;
            return true;
        }
    }
}
