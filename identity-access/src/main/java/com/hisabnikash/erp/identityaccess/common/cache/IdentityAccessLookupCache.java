package com.hisabnikash.erp.identityaccess.common.cache;

import com.hisabnikash.erp.cachesupport.CachedLookupResult;
import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.role.dto.RoleResponse;
import com.hisabnikash.erp.identityaccess.role.infrastructure.RoleRepository;
import com.hisabnikash.erp.identityaccess.user.application.UserResponseAssembler;
import com.hisabnikash.erp.identityaccess.user.domain.UserAccount;
import com.hisabnikash.erp.identityaccess.user.dto.UserResponse;
import com.hisabnikash.erp.identityaccess.user.infrastructure.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class IdentityAccessLookupCache {

    private final RoleRepository roleRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserResponseAssembler userResponseAssembler;

    @Cacheable(cacheNames = CacheNames.ROLE_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<RoleResponse> findRoleResponseById(UUID id) {
        return roleRepository.findById(id)
                .map(this::toRoleResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    @Cacheable(cacheNames = CacheNames.USER_BY_ID, key = "#id", sync = true)
    public CachedLookupResult<UserResponse> findUserResponseById(UUID id) {
        return userAccountRepository.findById(id)
                .map(userResponseAssembler::toResponse)
                .map(CachedLookupResult::found)
                .orElseGet(CachedLookupResult::notFound);
    }

    private RoleResponse toRoleResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getCode(),
                role.getName(),
                role.getDescription(),
                Set.copyOf(role.getPermissions()),
                role.getCreatedBy(),
                role.getCreatedAt(),
                role.getUpdatedBy(),
                role.getUpdatedAt()
        );
    }
}
