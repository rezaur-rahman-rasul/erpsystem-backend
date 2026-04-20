package com.hisabnikash.erp.identityaccess.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableConfigurationProperties(CacheStampedeProperties.class)
public class RedisCacheConfig {

    private static final String CACHE_PREFIX = "identity-access::v2::";

    @Bean
    RedisSerializer<Object> redisCacheValueSerializer(ObjectMapper objectMapper) {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        cacheObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.WRAPPER_ARRAY
        );
        return new GenericJackson2JsonRedisSerializer(cacheObjectMapper);
    }

    @Bean
    CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory,
            RedisSerializer<Object> redisCacheValueSerializer,
            CacheStampedeProperties cacheStampedeProperties
    ) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .computePrefixWith(cacheName -> CACHE_PREFIX + cacheName + "::")
                .entryTtl(new JitteredTtlFunction(
                        cacheStampedeProperties.getBaseTtl(),
                        cacheStampedeProperties.getTtlJitter()
                ))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisCacheValueSerializer));

        return new StampedeProtectedRedisCacheManager(
                redisConnectionFactory,
                cacheConfiguration,
                new RedisCacheEntryLockManager(redisConnectionFactory),
                cacheStampedeProperties
        );
    }
}
