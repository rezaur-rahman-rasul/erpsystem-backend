package com.hisabnikash.erp.gateway.security;

import com.hisabnikash.erp.gateway.config.properties.GatewayPolicyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestThrottlingFilterTest {

    @Mock
    private GatewayFilterChain chain;

    private RequestThrottlingFilter filter;
    private GatewayPolicyProperties policyProperties;

    @BeforeEach
    void setUp() {
        policyProperties = new GatewayPolicyProperties();
        policyProperties.getThrottling().setEnabled(true);
        policyProperties.getThrottling().setRequestsPerMinute(1);

        filter = new RequestThrottlingFilter(policyProperties);

        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void filterAllowsRequestWhenPathIsExempt() {
        MockServerWebExchange exchange = exchangeFor("/actuator/health", "127.0.0.1");

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
        assertThat(exchange.getResponse().getStatusCode()).isNull();
    }

    @Test
    void filterRejectsSecondRequestWhenRateLimitIsExceeded() {
        MockServerWebExchange firstRequest = exchangeFor("/organization/api/v1/branches", "127.0.0.1");
        MockServerWebExchange secondRequest = exchangeFor("/organization/api/v1/branches", "127.0.0.1");

        filter.filter(firstRequest, chain).block();
        filter.filter(secondRequest, chain).block();

        verify(chain, times(1)).filter(any(ServerWebExchange.class));
        assertThat(secondRequest.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(secondRequest.getResponse().getHeaders().getFirst("Retry-After")).isEqualTo("60");
    }

    private MockServerWebExchange exchangeFor(String path, String hostAddress) {
        MockServerHttpRequest request = MockServerHttpRequest.get(path)
                .remoteAddress(new InetSocketAddress(hostAddress, 8080))
                .build();

        return MockServerWebExchange.from(request);
    }
}
