package com.hisabnikash.erp.identityaccess.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record UpdateUserRequest(
        @NotBlank @Size(max = 60) String username,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 150) String displayName,
        @NotBlank @Size(max = 80) String tenantId,
        @NotEmpty Set<UUID> roleIds
) {
}
