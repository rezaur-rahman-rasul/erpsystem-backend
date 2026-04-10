package com.hisabnikash.erp.identityaccess.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hisabnikash.erp.identityaccess.organizationaccess.dto.OrganizationAccessResponse;
import com.hisabnikash.erp.identityaccess.user.dto.RoleSummaryResponse;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RedisCacheConfigTest {

    @Test
    void redisSerializerRoundTripsCachedUserLists() {
        RedisCacheConfig redisCacheConfig = new RedisCacheConfig();
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
        RedisSerializer<Object> serializer = redisCacheConfig.redisCacheValueSerializer(objectMapper);

        RoleSummaryResponse role = new RoleSummaryResponse(
                UUID.randomUUID(),
                "PLATFORM_ADMIN",
                "Platform Administrator"
        );
        UserResponse user = new UserResponse(
                UUID.randomUUID(),
                "admin",
                "admin@erp.local",
                "Platform Admin",
                "ERP-DEFAULT",
                com.hisabnikash.erp.identityaccess.user.domain.UserStatus.ACTIVE,
                "system",
                LocalDateTime.of(2026, 4, 10, 10, 30),
                "system",
                LocalDateTime.of(2026, 4, 10, 10, 30),
                new LinkedHashSet<>(List.of(role)),
                new LinkedHashSet<>(List.of(new OrganizationAccessResponse(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        null,
                        true,
                        "system",
                        LocalDateTime.of(2026, 4, 10, 10, 30),
                        "system",
                        LocalDateTime.of(2026, 4, 10, 10, 30)
                )))
        );
        List<UserResponse> users = new ArrayList<>(List.of(user));

        byte[] serialized = serializer.serialize(users);
        Object deserialized = serializer.deserialize(serialized);

        assertThat(serialized).isNotNull();
        assertThat(deserialized).isInstanceOf(List.class);
        assertThat((List<?>) deserialized).singleElement().isInstanceOf(UserResponse.class);

        UserResponse cachedUser = (UserResponse) ((List<?>) deserialized).get(0);
        assertThat(cachedUser.username()).isEqualTo("admin");
        assertThat(cachedUser.createdBy()).isEqualTo("system");
        assertThat(cachedUser.lastUpdatedBy()).isEqualTo("system");
        assertThat(cachedUser.roles()).singleElement().extracting(RoleSummaryResponse::code).isEqualTo("PLATFORM_ADMIN");
        assertThat(cachedUser.organizationAccesses()).singleElement().extracting(OrganizationAccessResponse::primaryAccess).isEqualTo(true);
    }
}
