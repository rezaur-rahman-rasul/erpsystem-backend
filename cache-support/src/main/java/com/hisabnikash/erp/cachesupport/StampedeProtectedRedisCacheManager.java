package com.hisabnikash.erp.cachesupport;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class StampedeProtectedRedisCacheManager extends RedisCacheManager {

    private final CacheEntryLockManager cacheEntryLockManager;
    private final CacheStampedeProperties cacheStampedeProperties;

    public StampedeProtectedRedisCacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration defaultCacheConfiguration,
            CacheEntryLockManager cacheEntryLockManager,
            CacheStampedeProperties cacheStampedeProperties
    ) {
        super(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory), defaultCacheConfiguration);

        Assert.notNull(cacheEntryLockManager, "Cache entry lock manager must not be null");
        Assert.notNull(cacheStampedeProperties, "Cache stampede properties must not be null");

        this.cacheEntryLockManager = cacheEntryLockManager;
        this.cacheStampedeProperties = cacheStampedeProperties;
    }

    @Override
    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfiguration) {
        RedisCacheConfiguration resolvedConfiguration =
                cacheConfiguration != null ? cacheConfiguration : getDefaultCacheConfiguration();

        return new StampedeProtectedRedisCache(
                name,
                getCacheWriter(),
                resolvedConfiguration,
                cacheEntryLockManager,
                cacheStampedeProperties.getLockKeyPrefix(),
                cacheStampedeProperties.getLockTtl(),
                cacheStampedeProperties.getRetryDelay()
        );
    }
}
