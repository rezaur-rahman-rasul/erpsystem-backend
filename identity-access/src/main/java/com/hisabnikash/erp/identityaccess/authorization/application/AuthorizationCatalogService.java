package com.hisabnikash.erp.identityaccess.authorization.application;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationAction;
import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResource;
import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResourceType;
import com.hisabnikash.erp.identityaccess.authorization.domain.ResourcePermission;
import com.hisabnikash.erp.identityaccess.authorization.dto.AuthorizationActionResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.AuthorizationResourceResponse;
import com.hisabnikash.erp.identityaccess.authorization.dto.ResourcePermissionResponse;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.AuthorizationActionRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.AuthorizationResourceRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.PermissionAliasRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.ResourcePermissionRepository;
import com.hisabnikash.erp.identityaccess.common.constants.CacheNames;
import com.hisabnikash.erp.identityaccess.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorizationCatalogService {

    private final AuthorizationActionRepository actionRepository;
    private final AuthorizationResourceRepository resourceRepository;
    private final ResourcePermissionRepository resourcePermissionRepository;
    private final PermissionAliasRepository permissionAliasRepository;

    @Cacheable(cacheNames = CacheNames.AUTHORIZATION_ACTIONS, key = "'ALL'")
    public List<AuthorizationActionResponse> getActions() {
        return actionRepository.findAll().stream()
                .sorted((left, right) -> left.getCode().compareToIgnoreCase(right.getCode()))
                .map(this::toActionResponse)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.AUTHORIZATION_PERMISSIONS, key = "'ALL'")
    public List<ResourcePermissionResponse> getPermissions() {
        return resourcePermissionRepository.findAllByOrderByPermissionKeyAsc().stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    @Cacheable(cacheNames = CacheNames.AUTHORIZATION_RESOURCES, key = "'ALL'")
    public List<AuthorizationResourceResponse> getResources(String query, String serviceCode, String resourceType) {
        return resourceRepository.findAllByOrderByFullCodeAsc().stream()
                .filter(resource -> matchesQuery(resource, query))
                .filter(resource -> matchesService(resource, serviceCode))
                .filter(resource -> matchesType(resource, resourceType))
                .map(this::toResourceResponse)
                .toList();
    }

    public AuthorizationResourceResponse getResource(String fullCode) {
        return resourceRepository.findByFullCodeIgnoreCase(fullCode.trim())
                .map(this::toResourceResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Authorization resource not found: " + fullCode));
    }

    public List<ResourcePermissionResponse> getPermissionsForResource(String fullCode) {
        AuthorizationResource resource = resourceRepository.findByFullCodeIgnoreCase(fullCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Authorization resource not found: " + fullCode));
        return resourcePermissionRepository.findByResourceOrderByPermissionKeyAsc(resource).stream()
                .map(this::toPermissionResponse)
                .toList();
    }

    private boolean matchesQuery(AuthorizationResource resource, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String normalized = query.trim().toUpperCase();
        return resource.getFullCode().toUpperCase().contains(normalized)
                || resource.getName().toUpperCase().contains(normalized);
    }

    private boolean matchesService(AuthorizationResource resource, String serviceCode) {
        return serviceCode == null || serviceCode.isBlank()
                || resource.getServiceCode().equalsIgnoreCase(serviceCode.trim());
    }

    private boolean matchesType(AuthorizationResource resource, String resourceType) {
        if (resourceType == null || resourceType.isBlank()) {
            return true;
        }
        return resource.getType() == AuthorizationResourceType.valueOf(resourceType.trim().toUpperCase());
    }

    private AuthorizationActionResponse toActionResponse(AuthorizationAction action) {
        return new AuthorizationActionResponse(
                action.getCode(),
                action.getName(),
                action.getAppliesToTypes(),
                action.getStatus().name()
        );
    }

    private AuthorizationResourceResponse toResourceResponse(AuthorizationResource resource) {
        return new AuthorizationResourceResponse(
                resource.getId(),
                resource.getCode(),
                resource.getFullCode(),
                resource.getName(),
                resource.getType().name(),
                resource.getParent() != null ? resource.getParent().getFullCode() : null,
                resource.getServiceCode(),
                resource.getPathDepth(),
                resource.getStatus().name(),
                resource.getMetadata()
        );
    }

    private ResourcePermissionResponse toPermissionResponse(ResourcePermission permission) {
        List<String> aliases = permissionAliasRepository.findByPermission_Id(permission.getId()).stream()
                .map(alias -> alias.getAliasCode())
                .sorted(String::compareToIgnoreCase)
                .toList();

        return new ResourcePermissionResponse(
                permission.getId(),
                permission.getPermissionKey(),
                permission.getResource().getFullCode(),
                permission.getAction().getCode(),
                permission.getServiceCode(),
                permission.getDescription(),
                permission.getStatus().name(),
                aliases
        );
    }
}
