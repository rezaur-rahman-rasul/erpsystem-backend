package com.hisabnikash.erp.gateway.config;

import com.hisabnikash.erp.cachesupport.CacheStampedeProperties;
import com.hisabnikash.erp.cachesupport.JitteredTtlFunction;
import com.hisabnikash.erp.cachesupport.RedisCacheEntryLockManager;
import com.hisabnikash.erp.cachesupport.StampedeProtectedRedisCacheManager;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(CacheStampedeProperties.class)
public class RedisCacheConfig {

    @Bean
    CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            CacheStampedeProperties cacheStampedeProperties
    ) {
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(new JitteredTtlFunction(
                        cacheStampedeProperties.getBaseTtl(),
                        cacheStampedeProperties.getTtlJitter()
                ))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

        return new StampedeProtectedRedisCacheManager(
                redisConnectionFactory,
                cacheConfiguration,
                new RedisCacheEntryLockManager(redisConnectionFactory),
                cacheStampedeProperties
        );
    }
}
