package com.hisabnikash.erp.masterdata.supplier.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.supplier.domain.Supplier;
import com.hisabnikash.erp.masterdata.supplier.dto.CreateSupplierRequest;
import com.hisabnikash.erp.masterdata.supplier.dto.SupplierResponse;
import com.hisabnikash.erp.masterdata.supplier.dto.UpdateSupplierRequest;
import com.hisabnikash.erp.masterdata.supplier.infrastructure.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final MasterDataOwnershipService ownershipService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_SUPPLIER")
    public SupplierResponse create(CreateSupplierRequest request) {
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (supplierRepository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, request.code())) {
            throw new DuplicateResourceException("Supplier code already exists: " + request.code());
        }

        Supplier supplier = new Supplier();
        apply(supplier, tenantId, legalEntityId, request.code(), request.name(), request.email(), request.phone(), request.taxNumber(), request.active());
        Supplier saved = supplierRepository.save(supplier);
        SupplierResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getSupplierCreated(),
                "SupplierCreated",
                "SUPPLIER",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<SupplierResponse> getAll() {
        return ownershipService.filterAccessible(supplierRepository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Supplier getById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
        return ownershipService.requireReadable(supplier, "Supplier");
    }

    @Auditable(action = "UPDATE_SUPPLIER")
    public SupplierResponse update(UUID id, UpdateSupplierRequest request) {
        Supplier supplier = getById(id);
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (supplierRepository.existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(legalEntityId, request.code(), id)) {
            throw new DuplicateResourceException("Supplier code already exists: " + request.code());
        }

        apply(supplier, tenantId, legalEntityId, request.code(), request.name(), request.email(), request.phone(), request.taxNumber(), request.active());
        Supplier saved = supplierRepository.save(supplier);
        SupplierResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getSupplierUpdated(),
                "SupplierUpdated",
                "SUPPLIER",
                saved.getId(),
                response
        );
        return response;
    }

    public SupplierResponse toResponse(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getTenantId(),
                supplier.getLegalEntityId(),
                supplier.getCode(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.getTaxNumber(),
                supplier.isActive(),
                supplier.getCreatedBy(),
                supplier.getCreatedAt(),
                supplier.getUpdatedBy(),
                supplier.getUpdatedAt()
        );
    }

    private void apply(Supplier supplier,
                       String tenantId,
                       UUID legalEntityId,
                       String code,
                       String name,
                       String email,
                       String phone,
                       String taxNumber,
                       boolean active) {
        supplier.setTenantId(tenantId);
        supplier.setLegalEntityId(legalEntityId);
        supplier.setCode(code.trim().toUpperCase());
        supplier.setName(name.trim());
        supplier.setEmail(normalizeLower(email));
        supplier.setPhone(normalize(phone));
        supplier.setTaxNumber(normalize(taxNumber));
        supplier.setActive(active);
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String normalizeLower(String value) {
        return value == null || value.isBlank() ? null : value.trim().toLowerCase();
    }
}
