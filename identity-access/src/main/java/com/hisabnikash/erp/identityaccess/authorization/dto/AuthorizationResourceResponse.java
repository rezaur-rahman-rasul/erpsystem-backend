package com.hisabnikash.erp.identityaccess.authorization.dto;

import java.util.UUID;

public record AuthorizationResourceResponse(
        UUID id,
        String code,
        String fullCode,
        String name,
        String type,
        String parentFullCode,
        String serviceCode,
        int pathDepth,
        String status,
        String metadata
) {
}
