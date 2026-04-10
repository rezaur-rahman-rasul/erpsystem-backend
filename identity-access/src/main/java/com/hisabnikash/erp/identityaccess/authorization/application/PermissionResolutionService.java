package com.hisabnikash.erp.identityaccess.authorization.application;

import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionAlias;
import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionAliasType;
import com.hisabnikash.erp.identityaccess.authorization.domain.ResourcePermission;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.PermissionAliasRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.ResourcePermissionRepository;
import com.hisabnikash.erp.identityaccess.common.exception.InvalidRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionResolutionService {

    private final ResourcePermissionRepository resourcePermissionRepository;
    private final PermissionAliasRepository permissionAliasRepository;

    public ResolvedPermission resolve(String codeOrAlias) {
        String normalized = normalize(codeOrAlias);

        Optional<ResourcePermission> directMatch = resourcePermissionRepository.findByPermissionKeyIgnoreCase(normalized);
        if (directMatch.isPresent()) {
            return new ResolvedPermission(directMatch.get(), resolveLegacyAuthority(directMatch.get()));
        }

        PermissionAlias alias = permissionAliasRepository.findByAliasCodeIgnoreCase(normalized)
                .orElseThrow(() -> new InvalidRequestException("Permission is not registered in the authorization catalog: " + codeOrAlias));
        return new ResolvedPermission(alias.getPermission(), alias.getAliasCode());
    }

    public Optional<String> resolvePermissionKeyIfPresent(String codeOrAlias) {
        if (codeOrAlias == null || codeOrAlias.isBlank()) {
            return Optional.empty();
        }
        return resourcePermissionRepository.findByPermissionKeyIgnoreCase(codeOrAlias.trim())
                .map(ResourcePermission::getPermissionKey)
                .or(() -> permissionAliasRepository.findByAliasCodeIgnoreCase(codeOrAlias.trim())
                        .map(alias -> alias.getPermission().getPermissionKey()));
    }

    public Set<ResolvedPermission> resolveAll(Collection<String> codesOrAliases) {
        return codesOrAliases.stream()
                .map(this::resolve)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    public String resolveLegacyAuthority(ResourcePermission permission) {
        return permissionAliasRepository.findFirstByPermission_IdAndAliasTypeOrderByAliasCodeAsc(permission.getId(), PermissionAliasType.LEGACY)
                .map(PermissionAlias::getAliasCode)
                .orElse(permission.getPermissionKey());
    }

    private String normalize(String codeOrAlias) {
        if (codeOrAlias == null || codeOrAlias.isBlank()) {
            throw new InvalidRequestException("Permission value must not be blank");
        }
        return codeOrAlias.trim();
    }

    public record ResolvedPermission(
            ResourcePermission permission,
            String legacyAuthority
    ) {
    }
}
