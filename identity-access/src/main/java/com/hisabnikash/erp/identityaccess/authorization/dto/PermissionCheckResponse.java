package com.hisabnikash.erp.identityaccess.authorization.dto;

import java.util.Set;
import java.util.UUID;

public record PermissionCheckResponse(
        UUID userId,
        boolean allowed,
        String permissionKey,
        Set<String> reasons
) {
}
