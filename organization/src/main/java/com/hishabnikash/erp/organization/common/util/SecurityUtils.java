package com.hishabnikash.erp.organization.common.util;

import com.hishabnikash.erp.organization.security.principal.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<CurrentUser> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CurrentUser currentUser)) {
            return Optional.empty();
        }

        return Optional.of(currentUser);
    }

    public static String getCurrentUserIdOrSystem() {
        return getCurrentUser()
                .map(CurrentUser::getUserId)
                .orElse("system");
    }

    public static String getCurrentTenantId() {
        return getCurrentUser()
                .map(CurrentUser::getTenantId)
                .orElse(null);
    }
}
