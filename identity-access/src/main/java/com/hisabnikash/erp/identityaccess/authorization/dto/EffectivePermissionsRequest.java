package com.hisabnikash.erp.identityaccess.authorization.dto;

import java.util.UUID;

public record EffectivePermissionsRequest(
        UUID userId
) {
}
