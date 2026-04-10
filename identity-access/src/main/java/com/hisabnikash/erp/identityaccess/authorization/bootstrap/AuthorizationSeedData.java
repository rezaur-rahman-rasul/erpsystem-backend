package com.hisabnikash.erp.identityaccess.authorization.bootstrap;

import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationAction;
import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResource;
import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationResourceType;
import com.hisabnikash.erp.identityaccess.authorization.domain.AuthorizationStatus;
import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionAlias;
import com.hisabnikash.erp.identityaccess.authorization.domain.PermissionAliasType;
import com.hisabnikash.erp.identityaccess.authorization.domain.ResourcePermission;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.AuthorizationActionRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.AuthorizationResourceRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.PermissionAliasRepository;
import com.hisabnikash.erp.identityaccess.authorization.infrastructure.ResourcePermissionRepository;
import com.hisabnikash.erp.identityaccess.authorization.seed.ActionSeedDefinition;
import com.hisabnikash.erp.identityaccess.authorization.seed.PermissionSeedDefinition;
import com.hisabnikash.erp.identityaccess.authorization.seed.PhaseOneAuthorizationCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(0)
public class AuthorizationSeedData implements CommandLineRunner {

    private final AuthorizationActionRepository actionRepository;
    private final AuthorizationResourceRepository resourceRepository;
    private final ResourcePermissionRepository permissionRepository;
    private final PermissionAliasRepository aliasRepository;

    @Override
    @Transactional
    public void run(String... args) {
        PhaseOneAuthorizationCatalog.actions().forEach(this::upsertAction);
        PhaseOneAuthorizationCatalog.permissions().forEach(this::upsertPermissionDefinition);
    }

    private void upsertAction(ActionSeedDefinition definition) {
        AuthorizationAction action = actionRepository.findById(definition.code())
                .orElseGet(AuthorizationAction::new);
        action.setCode(definition.code());
        action.setName(definition.name());
        action.setAppliesToTypes(definition.appliesToTypes());
        action.setStatus(AuthorizationStatus.ACTIVE);
        actionRepository.save(action);
    }

    private void upsertPermissionDefinition(PermissionSeedDefinition definition) {
        AuthorizationResource service = upsertResource(
                definition.serviceCode(),
                definition.serviceCode(),
                definition.serviceName(),
                AuthorizationResourceType.SERVICE,
                null,
                definition.serviceCode()
        );

        AuthorizationResource module = upsertResource(
                definition.serviceCode() + "." + definition.moduleCode(),
                definition.moduleCode(),
                definition.moduleName(),
                AuthorizationResourceType.MODULE,
                service,
                definition.serviceCode()
        );

        AuthorizationResource parent = module;
        if (definition.hasApiGroup()) {
            parent = upsertResource(
                    definition.apiGroupCode(),
                    "API",
                    definition.moduleName() + " APIs",
                    AuthorizationResourceType.API_GROUP,
                    module,
                    definition.serviceCode()
            );
        }

        AuthorizationResource resource = upsertResource(
                definition.fullResourceCode(),
                definition.resourceCode(),
                definition.resourceName(),
                definition.resourceType(),
                parent,
                definition.serviceCode()
        );

        ResourcePermission permission = permissionRepository.findByPermissionKeyIgnoreCase(definition.permissionKey())
                .orElseGet(ResourcePermission::new);
        permission.setResource(resource);
        permission.setAction(actionRepository.getReferenceById(definition.actionCode()));
        permission.setPermissionKey(definition.permissionKey());
        permission.setServiceCode(definition.serviceCode());
        permission.setDescription(definition.description());
        permission.setStatus(AuthorizationStatus.ACTIVE);
        permission = permissionRepository.save(permission);

        PermissionAlias alias = aliasRepository.findByAliasCodeIgnoreCase(definition.legacyCode())
                .orElseGet(PermissionAlias::new);
        alias.setPermission(permission);
        alias.setAliasCode(definition.legacyCode());
        alias.setAliasType(PermissionAliasType.LEGACY);
        aliasRepository.save(alias);
    }

    private AuthorizationResource upsertResource(String fullCode,
                                                 String code,
                                                 String name,
                                                 AuthorizationResourceType type,
                                                 AuthorizationResource parent,
                                                 String serviceCode) {
        AuthorizationResource resource = resourceRepository.findByFullCodeIgnoreCase(fullCode)
                .orElseGet(AuthorizationResource::new);
        resource.setCode(code);
        resource.setFullCode(fullCode);
        resource.setName(name);
        resource.setType(type);
        resource.setParent(parent);
        resource.setServiceCode(serviceCode);
        resource.setPathDepth((short) fullCode.split("\\.").length);
        resource.setSortOrder(0);
        resource.setStatus(AuthorizationStatus.ACTIVE);
        return resourceRepository.save(resource);
    }
}
