package com.hisabnikash.erp.identityaccess.authorization.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record PermissionCheckRequest(
        UUID userId,
        @NotBlank String resourceCode,
        @NotBlank String action
) {
}
