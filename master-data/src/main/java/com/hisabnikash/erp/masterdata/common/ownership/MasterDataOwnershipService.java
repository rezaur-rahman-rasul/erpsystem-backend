package com.hisabnikash.erp.masterdata.common.ownership;

import com.hisabnikash.erp.masterdata.common.entity.CompanyOwnedEntity;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.util.SecurityUtils;
import com.hisabnikash.erp.masterdata.integration.identity.domain.OrganizationAccessReference;
import com.hisabnikash.erp.masterdata.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hisabnikash.erp.masterdata.integration.organization.domain.BranchReference;
import com.hisabnikash.erp.masterdata.integration.organization.domain.LegalEntityReference;
import com.hisabnikash.erp.masterdata.integration.organization.infrastructure.BranchReferenceRepository;
import com.hisabnikash.erp.masterdata.integration.organization.infrastructure.LegalEntityReferenceRepository;
import com.hisabnikash.erp.masterdata.security.principal.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MasterDataOwnershipService {

    private final LegalEntityReferenceRepository legalEntityReferenceRepository;
    private final BranchReferenceRepository branchReferenceRepository;
    private final OrganizationAccessReferenceRepository organizationAccessRepository;

    public String requireCurrentTenantId() {
        String tenantId = SecurityUtils.getCurrentTenantId();
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant context is required for company-owned master data");
        }
        return tenantId.trim();
    }

    public UUID requireAccessibleLegalEntityId(UUID legalEntityId) {
        requireCurrentTenantId();
        LegalEntityReference legalEntity = legalEntityReferenceRepository.findById(legalEntityId)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity reference not available yet: " + legalEntityId));
        if (!legalEntity.isActive()) {
            throw new ResourceNotFoundException("Legal entity is not active: " + legalEntityId);
        }

        Set<UUID> accessibleLegalEntityIds = resolveAccessibleLegalEntityIds();
        if (accessibleLegalEntityIds != null && !accessibleLegalEntityIds.contains(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity is not accessible for current user: " + legalEntityId);
        }
        return legalEntityId;
    }

    public BranchOwnership requireAccessibleBranch(UUID branchId) {
        String tenantId = requireCurrentTenantId();
        BranchReference branchReference = branchReferenceRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch reference not available yet: " + branchId));
        if (!branchReference.isActive()) {
            throw new ResourceNotFoundException("Branch is not active: " + branchId);
        }

        List<OrganizationAccessReference> accessReferences = resolveCurrentUserAccessReferences();
        if (accessReferences != null && accessReferences.stream().noneMatch(reference -> grantsBranchAccess(reference, branchReference))) {
            throw new ResourceNotFoundException("Branch is not accessible for current user: " + branchId);
        }

        return new BranchOwnership(tenantId, branchReference.getLegalEntityId(), branchReference.getId());
    }

    public <T extends CompanyOwnedEntity> List<T> filterAccessible(Collection<T> entities) {
        return entities.stream()
                .filter(this::canAccess)
                .toList();
    }

    public <T extends CompanyOwnedEntity> T requireReadable(T entity, String aggregateName) {
        if (!canAccess(entity)) {
            throw new ResourceNotFoundException(aggregateName + " is not accessible for current user");
        }
        return entity;
    }

    private boolean canAccess(CompanyOwnedEntity entity) {
        Optional<CurrentUser> currentUser = SecurityUtils.getCurrentUser();
        if (currentUser.isEmpty()) {
            return true;
        }

        String currentTenantId = currentUser.get().getTenantId();
        if (entity.getTenantId() == null || !entity.getTenantId().equalsIgnoreCase(currentTenantId)) {
            return false;
        }

        Set<UUID> accessibleLegalEntityIds = resolveAccessibleLegalEntityIds();
        return accessibleLegalEntityIds == null || accessibleLegalEntityIds.contains(entity.getLegalEntityId());
    }

    private Set<UUID> resolveAccessibleLegalEntityIds() {
        List<OrganizationAccessReference> accessReferences = resolveCurrentUserAccessReferences();
        if (accessReferences == null || accessReferences.isEmpty()) {
            return null;
        }

        Set<UUID> legalEntityIds = new LinkedHashSet<>();
        accessReferences.stream()
                .map(OrganizationAccessReference::getLegalEntityId)
                .forEach(legalEntityIds::add);
        return legalEntityIds;
    }

    private List<OrganizationAccessReference> resolveCurrentUserAccessReferences() {
        return SecurityUtils.getCurrentUser()
                .flatMap(currentUser -> parseUuid(currentUser.getUserId())
                        .map(organizationAccessRepository::findByUserId))
                .orElse(null);
    }

    private boolean grantsBranchAccess(OrganizationAccessReference reference, BranchReference branchReference) {
        if (reference.getBranchId() != null) {
            return reference.getBranchId().equals(branchReference.getId());
        }
        return reference.getLegalEntityId().equals(branchReference.getLegalEntityId());
    }

    private Optional<UUID> parseUuid(String value) {
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
