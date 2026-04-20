package com.hisabnikash.erp.cachesupport;

import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HexFormat;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.LockSupport;

public class StampedeProtectedRedisCache extends RedisCache {

    private final CacheEntryLockManager cacheEntryLockManager;
    private final String lockKeyPrefix;
    private final Duration lockTtl;
    private final Duration retryDelay;

    public StampedeProtectedRedisCache(
            String name,
            RedisCacheWriter cacheWriter,
            RedisCacheConfiguration cacheConfiguration,
            CacheEntryLockManager cacheEntryLockManager,
            String lockKeyPrefix,
            Duration lockTtl,
            Duration retryDelay
    ) {
        super(name, cacheWriter, cacheConfiguration);

        Assert.notNull(cacheEntryLockManager, "Cache entry lock manager must not be null");
        Assert.hasText(lockKeyPrefix, "Lock key prefix must not be blank");
        Assert.notNull(lockTtl, "Lock TTL must not be null");
        Assert.notNull(retryDelay, "Retry delay must not be null");
        Assert.isTrue(!lockTtl.isZero() && !lockTtl.isNegative(), "Lock TTL must be positive");
        Assert.isTrue(!retryDelay.isNegative(), "Retry delay must not be negative");

        this.cacheEntryLockManager = cacheEntryLockManager;
        this.lockKeyPrefix = lockKeyPrefix;
        this.lockTtl = lockTtl;
        this.retryDelay = retryDelay;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper cachedValue = super.get(key);
        if (cachedValue != null) {
            return (T) cachedValue.get();
        }

        String lockKey = createLockKey(key);
        String token = UUID.randomUUID().toString();

        while (true) {
            if (cacheEntryLockManager.tryLock(lockKey, token, lockTtl)) {
                try {
                    ValueWrapper cachedAfterLock = super.get(key);
                    return cachedAfterLock != null ? (T) cachedAfterLock.get() : loadCacheValue(key, valueLoader);
                } finally {
                    cacheEntryLockManager.unlock(lockKey, token);
                }
            }

            ValueWrapper refreshedValue = super.get(key);
            if (refreshedValue != null) {
                return (T) refreshedValue.get();
            }

            pauseBeforeRetry();
        }
    }

    private String createLockKey(Object key) {
        byte[] digest = DigestUtils.md5Digest(createCacheKey(key).getBytes(StandardCharsets.UTF_8));
        return lockKeyPrefix + getName() + "::" + HexFormat.of().formatHex(digest);
    }

    private void pauseBeforeRetry() {
        if (retryDelay.isZero()) {
            Thread.onSpinWait();
            return;
        }

        LockSupport.parkNanos(retryDelay.toNanos());
        if (Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for cache refresh lock on cache " + getName());
        }
    }
}
