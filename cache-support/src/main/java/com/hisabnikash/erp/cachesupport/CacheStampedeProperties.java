package com.hisabnikash.erp.cachesupport;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.cache.stampede")
public class CacheStampedeProperties {

    private Duration baseTtl = Duration.ofMinutes(10);
    private Duration ttlJitter = Duration.ofMinutes(2);
    private Duration lockTtl = Duration.ofSeconds(30);
    private Duration retryDelay = Duration.ofMillis(50);
    private String lockKeyPrefix = "cache-lock::";

    public Duration getBaseTtl() {
        return baseTtl;
    }

    public void setBaseTtl(Duration baseTtl) {
        this.baseTtl = baseTtl;
    }

    public Duration getTtlJitter() {
        return ttlJitter;
    }

    public void setTtlJitter(Duration ttlJitter) {
        this.ttlJitter = ttlJitter;
    }

    public Duration getLockTtl() {
        return lockTtl;
    }

    public void setLockTtl(Duration lockTtl) {
        this.lockTtl = lockTtl;
    }

    public Duration getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(Duration retryDelay) {
        this.retryDelay = retryDelay;
    }

    public String getLockKeyPrefix() {
        return lockKeyPrefix;
    }

    public void setLockKeyPrefix(String lockKeyPrefix) {
        this.lockKeyPrefix = lockKeyPrefix;
    }
}
