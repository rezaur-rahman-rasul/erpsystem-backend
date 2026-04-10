package com.hisabnikash.erp.identityaccess.role.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record RoleResponse(
        UUID id,
        String code,
        String name,
        String description,
        Set<String> permissions,
        String createdBy,
        LocalDateTime createdAt,
        String lastUpdatedBy,
        LocalDateTime lastUpdatedAt
) {
}
