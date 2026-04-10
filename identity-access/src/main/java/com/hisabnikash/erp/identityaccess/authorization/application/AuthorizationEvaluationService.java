package com.hisabnikash.erp.identityaccess.authorization.application;

import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionEffect;
import com.hisabnikash.erp.identityaccess.authorization.dto.EffectivePermissionsResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.PermissionCheckResponse;
import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.identityaccess.common.exception.UnauthorizedException;
import com.hisabnikash.erp.identityaccess.common.util.SecurityUtils;
import com.hisabnikash.erp.identityaccess.security.principal.CurrentUser;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorizationEvaluationService {

    private static final String ADMIN_VIEW_AUTHORITY = "identity:permission:view";

    private final UserAccountRepository userAccountRepository;
    private final PermissionResolutionService permissionResolutionService;

    @Cacheable(cacheNames = CacheNames.AUTHORIZATION_EFFECTIVE_PERMISSIONS, key = "#requestedUserId != null ? #requestedUserId.toString() : 'SELF:' + T(com.hisabnikash.erp.identityaccess.common.util.SecurityUtils).getCurrentUserId().toString()")
    public EffectivePermissionsResponse getEffectivePermissions(UUID requestedUserId) {
        UserAccount user = resolveRequestedUser(requestedUserId);
        return new EffectivePermissionsResponse(user.getId(), collectEffectivePermissionKeys(user));
    }

    public PermissionCheckResponse checkPermission(UUID requestedUserId, String resourceCode, String action) {
        UserAccount user = resolveRequestedUser(requestedUserId);
        String permissionKey = resourceCode.trim().toUpperCase() + "#" + action.trim().toUpperCase();
        Set<String> effectivePermissions = collectEffectivePermissionKeys(user);
        boolean allowed = effectivePermissions.contains(permissionKey);
        Set<String> reasons = new LinkedHashSet<>();
        reasons.add(allowed ? "EFFECTIVE_PERMISSION_MATCH" : "DEFAULT_DENY");
        return new PermissionCheckResponse(user.getId(), allowed, permissionKey, reasons);
    }

    private UserAccount resolveRequestedUser(UUID requestedUserId) {
        UUID currentUserId = SecurityUtils.getCurrentUserId();
        UUID targetUserId = requestedUserId != null ? requestedUserId : currentUserId;

        if (!currentUserId.equals(targetUserId) && !currentUserHasAuthority(ADMIN_VIEW_AUTHORITY)) {
            throw new UnauthorizedException("You are not allowed to inspect permissions for another user");
        }

        return userAccountRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + targetUserId));
    }

    private boolean currentUserHasAuthority(String authority) {
        CurrentUser currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not available"));
        return currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equalsIgnoreCase);
    }

    private Set<String> collectEffectivePermissionKeys(UserAccount user) {
        Set<String> allowed = new LinkedHashSet<>();
        Set<String> denied = new LinkedHashSet<>();

        user.getRoles().forEach(role -> {
            role.getPermissionGrants().forEach(grant -> {
                String permissionKey = grant.getPermission().getPermissionKey();
                if (grant.getEffect() == PermissionEffect.DENY) {
                    denied.add(permissionKey);
                } else {
                    allowed.add(permissionKey);
                }
            });

            role.getPermissions().forEach(legacyPermission ->
                    permissionResolutionService.resolvePermissionKeyIfPresent(legacyPermission)
                            .ifPresent(allowed::add)
            );
        });

        allowed.removeAll(denied);
        return allowed;
    }
}
