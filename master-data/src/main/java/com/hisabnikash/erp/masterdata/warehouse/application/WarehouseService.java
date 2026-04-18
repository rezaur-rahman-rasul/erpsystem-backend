package com.hisabnikash.erp.masterdata.warehouse.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.ownership.BranchOwnership;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.util.SecurityUtils;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.integration.identity.domain.OrganizationAccessReference;
import com.hisabnikash.erp.masterdata.integration.identity.infrastructure.OrganizationAccessReferenceRepository;
import com.hisabnikash.erp.masterdata.integration.organization.domain.BranchReference;
import com.hisabnikash.erp.masterdata.integration.organization.infrastructure.BranchReferenceRepository;
import com.hisabnikash.erp.masterdata.warehouse.domain.Warehouse;
import com.hisabnikash.erp.masterdata.warehouse.dto.CreateWarehouseRequest;
import com.hisabnikash.erp.masterdata.warehouse.dto.UpdateWarehouseRequest;
import com.hisabnikash.erp.masterdata.warehouse.dto.WarehouseResponse;
import com.hisabnikash.erp.masterdata.warehouse.infrastructure.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final BranchReferenceRepository branchReferenceRepository;
    private final OrganizationAccessReferenceRepository organizationAccessRepository;
    private final MasterDataOwnershipService ownershipService;

    @Auditable(action = "CREATE_WAREHOUSE")
    public WarehouseResponse create(CreateWarehouseRequest request) {
        BranchOwnership branchOwnership = ownershipService.requireAccessibleBranch(request.branchId());
        if (warehouseRepository.existsByLegalEntityIdAndCodeIgnoreCase(branchOwnership.legalEntityId(), request.code())) {
            throw new DuplicateResourceException("Warehouse code already exists: " + request.code());
        }

        Warehouse warehouse = new Warehouse();
        apply(
                warehouse,
                branchOwnership.tenantId(),
                branchOwnership.legalEntityId(),
                request.code(),
                request.name(),
                request.branchId(),
                request.locationCode(),
                request.active()
        );
        Warehouse saved = warehouseRepository.save(warehouse);
        WarehouseResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getWarehouseCreated(),
                "WarehouseCreated",
                "WAREHOUSE",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAll() {
        Set<UUID> accessibleBranchIds = resolveAccessibleBranchIds();
        List<Warehouse> warehouses = accessibleBranchIds == null
                ? warehouseRepository.findAll()
                : accessibleBranchIds.isEmpty()
                ? List.of()
                : warehouseRepository.findByBranchIdIn(accessibleBranchIds);
        return ownershipService.filterAccessible(warehouses).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Warehouse getById(UUID id) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + id));
        ownershipService.requireReadable(warehouse, "Warehouse");
        enforceBranchAccess(warehouse.getBranchId());
        return warehouse;
    }

    @Auditable(action = "UPDATE_WAREHOUSE")
    public WarehouseResponse update(UUID id, UpdateWarehouseRequest request) {
        Warehouse warehouse = getById(id);
        BranchOwnership branchOwnership = ownershipService.requireAccessibleBranch(request.branchId());
        if (warehouseRepository.existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(branchOwnership.legalEntityId(), request.code(), id)) {
            throw new DuplicateResourceException("Warehouse code already exists: " + request.code());
        }

        apply(
                warehouse,
                branchOwnership.tenantId(),
                branchOwnership.legalEntityId(),
                request.code(),
                request.name(),
                request.branchId(),
                request.locationCode(),
                request.active()
        );
        Warehouse saved = warehouseRepository.save(warehouse);
        WarehouseResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getWarehouseUpdated(),
                "WarehouseUpdated",
                "WAREHOUSE",
                saved.getId(),
                response
        );
        return response;
    }

    public WarehouseResponse toResponse(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getTenantId(),
                warehouse.getLegalEntityId(),
                warehouse.getCode(),
                warehouse.getName(),
                warehouse.getBranchId(),
                warehouse.getLocationCode(),
                warehouse.isActive(),
                warehouse.getCreatedBy(),
                warehouse.getCreatedAt(),
                warehouse.getUpdatedBy(),
                warehouse.getUpdatedAt()
        );
    }

    private void apply(Warehouse warehouse,
                       String tenantId,
                       UUID legalEntityId,
                       String code,
                       String name,
                       UUID branchId,
                       String locationCode,
                       boolean active) {
        warehouse.setTenantId(tenantId);
        warehouse.setLegalEntityId(legalEntityId);
        warehouse.setCode(code.trim().toUpperCase());
        warehouse.setName(name.trim());
        warehouse.setBranchId(branchId);
        warehouse.setLocationCode(normalize(locationCode));
        warehouse.setActive(active);
    }

    private void validateBranch(UUID branchId) {
        BranchReference branchReference = branchReferenceRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch reference not available yet: " + branchId));
        if (!branchReference.isActive()) {
            throw new ResourceNotFoundException("Branch is not active: " + branchId);
        }
    }

    private void enforceBranchAccess(UUID branchId) {
        Set<UUID> accessibleBranchIds = resolveAccessibleBranchIds();
        if (accessibleBranchIds != null && !accessibleBranchIds.contains(branchId)) {
            throw new ResourceNotFoundException("Branch is not accessible for current user: " + branchId);
        }
    }

    private Set<UUID> resolveAccessibleBranchIds() {
        return SecurityUtils.getCurrentUser()
                .flatMap(currentUser -> parseUserId(currentUser.getUserId())
                        .map(organizationAccessRepository::findByUserId))
                .map(this::expandAccessibleBranchIds)
                .orElse(null);
    }

    private Set<UUID> expandAccessibleBranchIds(List<OrganizationAccessReference> accessReferences) {
        if (accessReferences.isEmpty()) {
            return null;
        }

        Set<UUID> branchIds = new LinkedHashSet<>();
        for (OrganizationAccessReference accessReference : accessReferences) {
            if (accessReference.getBranchId() != null) {
                branchReferenceRepository.findById(accessReference.getBranchId())
                        .filter(BranchReference::isActive)
                        .map(BranchReference::getId)
                        .ifPresent(branchIds::add);
                continue;
            }

            branchReferenceRepository.findByLegalEntityIdAndActiveTrue(accessReference.getLegalEntityId()).stream()
                    .map(BranchReference::getId)
                    .forEach(branchIds::add);
        }
        return branchIds;
    }

    private java.util.Optional<UUID> parseUserId(String userId) {
        try {
            return java.util.Optional.of(UUID.fromString(userId));
        } catch (IllegalArgumentException ex) {
            return java.util.Optional.empty();
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
