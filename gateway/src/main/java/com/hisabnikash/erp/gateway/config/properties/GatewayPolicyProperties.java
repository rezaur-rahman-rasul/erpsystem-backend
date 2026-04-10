package com.hisabnikash.erp.gateway.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.gateway")
public class GatewayPolicyProperties {

    private Throttling throttling = new Throttling();
    private List<String> tenantAwarePrefixes = new ArrayList<>();

    public static class Throttling {
        private boolean enabled = true;
        private int requestsPerMinute = 120;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }

    public Throttling getThrottling() {
        return throttling;
    }

    public void setThrottling(Throttling throttling) {
        this.throttling = throttling;
    }

    public List<String> getTenantAwarePrefixes() {
        return tenantAwarePrefixes;
    }

    public void setTenantAwarePrefixes(List<String> tenantAwarePrefixes) {
        this.tenantAwarePrefixes = tenantAwarePrefixes;
    }
}
