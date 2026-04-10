package com.hisabnikash.erp.identityaccess.auth.dto;

import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String refreshToken,
        UserResponse user
) {
}
