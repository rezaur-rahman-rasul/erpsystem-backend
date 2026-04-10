package com.hisabnikash.erp.identityaccess.authorization.dto;

import java.util.List;
import java.util.UUID;

public record ResourcePermissionResponse(
        UUID id,
        String permissionKey,
        String resourceCode,
        String actionCode,
        String serviceCode,
        String description,
        String status,
        List<String> aliases
) {
}
