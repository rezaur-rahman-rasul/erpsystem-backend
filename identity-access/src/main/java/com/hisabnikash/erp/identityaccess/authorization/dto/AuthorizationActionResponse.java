package com.hisabnikash.erp.identityaccess.authorization.dto;

public record AuthorizationActionResponse(
        String code,
        String name,
        String appliesToTypes,
        String status
) {
}
