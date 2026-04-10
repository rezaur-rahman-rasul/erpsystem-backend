package com.hisabnikash.erp.identityaccess.user.dto;

import com.hisabnikash.erp.identityaccess.user.domain.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(
        @NotNull UserStatus status
) {
}
