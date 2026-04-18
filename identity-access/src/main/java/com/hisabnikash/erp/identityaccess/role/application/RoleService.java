package com.hisabnikash.erp.identityaccess.role.application;

import com.hisabnikash.erp.identityaccess.authorization.application.PermissionResolutionService;
import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionEffect;
import com.hisabnikash.erp.identityaccess.authorization.domain.RolePermissionGrant;
import com.hisabnikash.erp.identityaccess.audit.aop.Auditable;
import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.identityaccess.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.identityaccess.config.properties.MessagingProperties;
import com.hisabnikash.erp.identityaccess.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.identityaccess.role.domain.Role;
import com.hisabnikash.erp.identityaccess.role.dto.CreateRoleRequest;
import com.hisabnikash.erp.identityaccess.role.dto.RoleResponse;
import com.hisabnikash.erp.identityaccess.role.dto.UpdateRoleRequest;
import com.hisabnikash.erp.identityaccess.role.infrastructure.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionResolutionService permissionResolutionService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_ROLE")
    @CacheEvict(
            cacheNames = {
                    CacheNames.ROLE_BY_ID,
                    CacheNames.ROLE_LIST,
                    CacheNames.USER_BY_ID,
                    CacheNames.USER_LIST
            },
            allEntries = true
    )
    public RoleResponse create(CreateRoleRequest request) {
        String code = normalizeRoleCode(request.code());
        if (roleRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Role code already exists: " + code);
        }

        Role role = new Role();
        role.setCode(code);
        role.setName(normalizeText(request.name()));
        role.setDescription(request.description());
        applyPermissions(role, request.permissions());

        return saveAndPublish(
                role,
                messagingProperties.getTopics().getRoleCreated(),
                "RoleCreated"
        );
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ROLE_LIST, key = "'ALL'")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Role getById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.ROLE_BY_ID, key = "#id")
    public RoleResponse getResponseById(UUID id) {
        return toResponse(getById(id));
    }

    @Auditable(action = "UPDATE_ROLE")
    @CacheEvict(
            cacheNames = {
                    CacheNames.ROLE_BY_ID,
                    CacheNames.ROLE_LIST,
                    CacheNames.USER_BY_ID,
                    CacheNames.USER_LIST
            },
            allEntries = true
    )
    public RoleResponse update(UUID id, UpdateRoleRequest request) {
        Role role = getById(id);
        String code = normalizeRoleCode(request.code());

        if (roleRepository.existsByCodeIgnoreCaseAndIdNot(code, id)) {
            throw new DuplicateResourceException("Role code already exists: " + code);
        }

        role.setCode(code);
        role.setName(normalizeText(request.name()));
        role.setDescription(request.description());
        applyPermissions(role, request.permissions());

        return saveAndPublish(
                role,
                messagingProperties.getTopics().getRoleUpdated(),
                "RoleUpdated"
        );
    }

    public RoleResponse toResponse(Role role) {
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

    public void applyPermissions(Role role, Collection<String> permissions) {
        Set<PermissionResolutionService.ResolvedPermission> resolvedPermissions =
                permissionResolutionService.resolveAll(permissions);

        Set<String> legacyAuthorities = new LinkedHashSet<>();
        for (PermissionResolutionService.ResolvedPermission resolvedPermission : resolvedPermissions) {
            legacyAuthorities.add(resolvedPermission.legacyAuthority());
        }

        role.setPermissions(legacyAuthorities);
        syncPermissionGrants(role, resolvedPermissions);
    }

    private RoleResponse saveAndPublish(Role role, String topic, String eventType) {
        Role saved = roleRepository.save(role);
        RoleResponse response = toResponse(saved);

        eventPublisher.publish(
                topic,
                eventType,
                "ROLE",
                saved.getId(),
                response
        );

        return response;
    }

    private void syncPermissionGrants(
            Role role,
            Set<PermissionResolutionService.ResolvedPermission> resolvedPermissions
    ) {
        Map<UUID, PermissionResolutionService.ResolvedPermission> desiredByPermissionId =
                new LinkedHashMap<>();

        for (PermissionResolutionService.ResolvedPermission resolvedPermission : resolvedPermissions) {
            UUID permissionId = resolvedPermission.permission().getId();
            desiredByPermissionId.putIfAbsent(permissionId, resolvedPermission);
        }

        Set<UUID> seenPermissionIds = new LinkedHashSet<>();
        Iterator<RolePermissionGrant> iterator = role.getPermissionGrants().iterator();

        while (iterator.hasNext()) {
            RolePermissionGrant existingGrant = iterator.next();
            UUID permissionId = existingGrant.getPermission().getId();
            PermissionResolutionService.ResolvedPermission desiredPermission =
                    desiredByPermissionId.get(permissionId);

            boolean duplicateInCollection = !seenPermissionIds.add(permissionId);
            boolean permissionRemoved = desiredPermission == null;
            boolean wrongEffect = desiredPermission != null
                    && existingGrant.getEffect() != PermissionEffect.ALLOW;

            if (duplicateInCollection || permissionRemoved) {
                iterator.remove();
                continue;
            }

            if (wrongEffect) {
                existingGrant.setEffect(PermissionEffect.ALLOW);
            }
        }

        Set<UUID> existingPermissionIds = new LinkedHashSet<>();
        for (RolePermissionGrant existingGrant : role.getPermissionGrants()) {
            existingPermissionIds.add(existingGrant.getPermission().getId());
        }

        for (PermissionResolutionService.ResolvedPermission resolvedPermission :
                desiredByPermissionId.values()) {
            UUID permissionId = resolvedPermission.permission().getId();
            if (existingPermissionIds.contains(permissionId)) {
                continue;
            }

            RolePermissionGrant grant = new RolePermissionGrant();
            grant.setRole(role);
            grant.setPermission(resolvedPermission.permission());
            grant.setEffect(PermissionEffect.ALLOW);
            role.getPermissionGrants().add(grant);
        }
    }

    private String normalizeRoleCode(String value) {
        return value.trim().toUpperCase();
    }

    private String normalizeText(String value) {
        return value.trim();
    }
}
