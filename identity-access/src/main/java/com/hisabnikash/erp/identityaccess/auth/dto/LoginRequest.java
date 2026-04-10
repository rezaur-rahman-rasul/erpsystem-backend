package com.hisabnikash.erp.identityaccess.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank @Size(max = 120) String identifier,
        @NotBlank @Size(max = 120) String password,
        @Size(max = 80) String tenantId
) {
}
