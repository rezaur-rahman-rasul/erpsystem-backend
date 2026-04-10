package com.hisabnikash.erp.identityaccess.authorization.dto;

import java.util.Set;
import java.util.UUID;

public record EffectivePermissionsResponse(
        UUID userId,
        Set<String> permissions
) {
}
