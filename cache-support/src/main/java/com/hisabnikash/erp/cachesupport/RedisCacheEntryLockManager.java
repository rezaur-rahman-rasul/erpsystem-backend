package com.hisabnikash.erp.cachesupport;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedisCacheEntryLockManager implements CacheEntryLockManager {

    private static final byte[] UNLOCK_SCRIPT = """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            end
            return 0
            """.getBytes(StandardCharsets.UTF_8);

    private final RedisConnectionFactory connectionFactory;

    public RedisCacheEntryLockManager(RedisConnectionFactory connectionFactory) {
        Assert.notNull(connectionFactory, "Redis connection factory must not be null");
        this.connectionFactory = connectionFactory;
    }

    @Override
    public boolean tryLock(String lockKey, String token, Duration ttl) {
        Assert.hasText(lockKey, "Lock key must not be blank");
        Assert.hasText(token, "Lock token must not be blank");
        Assert.notNull(ttl, "Lock TTL must not be null");
        Assert.isTrue(!ttl.isZero() && !ttl.isNegative(), "Lock TTL must be positive");

        try (RedisConnection connection = connectionFactory.getConnection()) {
            Boolean locked = connection.stringCommands().set(
                    toBytes(lockKey),
                    toBytes(token),
                    Expiration.from(ttl.toMillis(), TimeUnit.MILLISECONDS),
                    SetOption.SET_IF_ABSENT
            );
            return Boolean.TRUE.equals(locked);
        }
    }

    @Override
    public void unlock(String lockKey, String token) {
        Assert.hasText(lockKey, "Lock key must not be blank");
        Assert.hasText(token, "Lock token must not be blank");

        try (RedisConnection connection = connectionFactory.getConnection()) {
            connection.scriptingCommands().eval(
                    UNLOCK_SCRIPT,
                    ReturnType.INTEGER,
                    1,
                    toBytes(lockKey),
                    toBytes(token)
            );
        }
    }

    private byte[] toBytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }
}
