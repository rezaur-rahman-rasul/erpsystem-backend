package com.hisabnikash.erp.cachesupport;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.CacheStatistics;
import org.springframework.data.redis.cache.CacheStatisticsCollector;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class StampedeProtectedRedisCacheTest {

    @Test
    void loadsValueOnlyOnceAcrossConcurrentMissesAfterExpiry() throws Exception {
        InMemoryRedisCacheWriter cacheWriter = new InMemoryRedisCacheWriter();
        InMemoryCacheEntryLockManager lockManager = new InMemoryCacheEntryLockManager();
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        StampedeProtectedRedisCache firstCache = new StampedeProtectedRedisCache(
                "users",
                cacheWriter,
                configuration,
                lockManager,
                "lock::",
                Duration.ofSeconds(5),
                Duration.ofMillis(5)
        );
        StampedeProtectedRedisCache secondCache = new StampedeProtectedRedisCache(
                "users",
                cacheWriter,
                configuration,
                lockManager,
                "lock::",
                Duration.ofSeconds(5),
                Duration.ofMillis(5)
        );

        firstCache.put("ALL", "warm");
        firstCache.evict("ALL");

        AtomicInteger loadCount = new AtomicInteger();
        CountDownLatch startLatch = new CountDownLatch(1);
        Callable<String> loader = () -> {
            loadCount.incrementAndGet();
            Thread.sleep(75);
            return "reloaded";
        };

        ExecutorService executorService = Executors.newFixedThreadPool(12);
        try {
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                StampedeProtectedRedisCache cache = i % 2 == 0 ? firstCache : secondCache;
                futures.add(executorService.submit(() -> {
                    startLatch.await();
                    return cache.get("ALL", loader);
                }));
            }

            startLatch.countDown();

            List<String> results = new ArrayList<>();
            for (Future<String> future : futures) {
                results.add(future.get());
            }

            assertThat(results).containsOnly("reloaded");
            assertThat(loadCount).hasValue(1);
            assertThat(firstCache.get("ALL", () -> "unexpected")).isEqualTo("reloaded");
        } finally {
            executorService.shutdownNow();
        }
    }

    private static final class InMemoryCacheEntryLockManager implements CacheEntryLockManager {

        private final Map<String, String> locks = new ConcurrentHashMap<>();

        @Override
        public boolean tryLock(String lockKey, String token, Duration ttl) {
            return locks.putIfAbsent(lockKey, token) == null;
        }

        @Override
        public void unlock(String lockKey, String token) {
            locks.remove(lockKey, token);
        }
    }

    private static final class InMemoryRedisCacheWriter implements RedisCacheWriter {

        private final Map<CacheKey, byte[]> values = new ConcurrentHashMap<>();

        @Override
        public byte[] get(String name, byte[] key) {
            return values.get(new CacheKey(name, encode(key)));
        }

        @Override
        public CompletableFuture<byte[]> retrieve(String name, byte[] key, Duration ttl) {
            return CompletableFuture.completedFuture(get(name, key));
        }

        @Override
        public void put(String name, byte[] key, byte[] value, Duration ttl) {
            values.put(new CacheKey(name, encode(key)), value);
        }

        @Override
        public CompletableFuture<Void> store(String name, byte[] key, byte[] value, Duration ttl) {
            put(name, key, value, ttl);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public byte[] putIfAbsent(String name, byte[] key, byte[] value, Duration ttl) {
            return values.putIfAbsent(new CacheKey(name, encode(key)), value);
        }

        @Override
        public void remove(String name, byte[] key) {
            values.remove(new CacheKey(name, encode(key)));
        }

        @Override
        public void clean(String name, byte[] pattern) {
            values.keySet().removeIf(cacheKey -> cacheKey.name.equals(name));
        }

        @Override
        public void clearStatistics(String name) {
        }

        @Override
        public CacheStatistics getCacheStatistics(String cacheName) {
            return CacheStatisticsCollector.none().getCacheStatistics(cacheName);
        }

        @Override
        public RedisCacheWriter withStatisticsCollector(CacheStatisticsCollector cacheStatisticsCollector) {
            return this;
        }

        private String encode(byte[] key) {
            return Base64.getEncoder().encodeToString(key);
        }

        private record CacheKey(String name, String key) {
        }
    }
}
