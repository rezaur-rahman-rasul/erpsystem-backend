package com.hisabnikash.erp.cachesupport;

import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.LongSupplier;

public final class JitteredTtlFunction implements RedisCacheWriter.TtlFunction {

    private final Duration baseTtl;
    private final Duration ttlJitter;
    private final LongSupplier jitterSupplier;

    public JitteredTtlFunction(Duration baseTtl, Duration ttlJitter) {
        this(baseTtl, ttlJitter, () -> ThreadLocalRandom.current().nextLong(ttlJitter.toMillis() + 1));
    }

    JitteredTtlFunction(Duration baseTtl, Duration ttlJitter, LongSupplier jitterSupplier) {
        Assert.notNull(baseTtl, "Base TTL must not be null");
        Assert.notNull(ttlJitter, "TTL jitter must not be null");
        Assert.notNull(jitterSupplier, "Jitter supplier must not be null");
        Assert.isTrue(!baseTtl.isNegative(), "Base TTL must not be negative");
        Assert.isTrue(!ttlJitter.isNegative(), "TTL jitter must not be negative");

        this.baseTtl = baseTtl;
        this.ttlJitter = ttlJitter;
        this.jitterSupplier = jitterSupplier;
    }

    @Override
    public Duration getTimeToLive(Object key, @Nullable Object value) {
        if (ttlJitter.isZero()) {
            return baseTtl;
        }

        long maxJitterMillis = ttlJitter.toMillis();
        if (maxJitterMillis <= 0) {
            return baseTtl;
        }

        long baseMillis = baseTtl.toMillis();
        long jitterMillis = Math.max(0, Math.min(maxJitterMillis, jitterSupplier.getAsLong()));
        return Duration.ofMillis(baseMillis + jitterMillis);
    }
}
