package com.hisabnikash.erp.identityaccess.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
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
    RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(RedisSerializer<Object> redisCacheValueSerializer) {
        return builder -> builder.cacheDefaults(
                RedisCacheConfiguration.defaultCacheConfig()
                        .computePrefixWith(cacheName -> CACHE_PREFIX + cacheName + "::")
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues()
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisCacheValueSerializer))
        );
    }
}
