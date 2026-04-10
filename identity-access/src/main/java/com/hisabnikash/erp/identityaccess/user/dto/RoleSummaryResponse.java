package com.hisabnikash.erp.identityaccess.user.dto;

import java.util.UUID;

public record RoleSummaryResponse(
        UUID id,
        String code,
        String name
) {
}
