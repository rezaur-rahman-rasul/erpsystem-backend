package com.hisabnikash.erp.masterdata.taxcode.application;

import com.hisabnikash.erp.masterdata.audit.aop.Auditable;
import com.hisabnikash.erp.masterdata.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.masterdata.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.masterdata.common.ownership.MasterDataOwnershipService;
import com.hisabnikash.erp.masterdata.config.properties.MessagingProperties;
import com.hisabnikash.erp.masterdata.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.masterdata.taxcode.domain.TaxCode;
import com.hisabnikash.erp.masterdata.taxcode.dto.CreateTaxCodeRequest;
import com.hisabnikash.erp.masterdata.taxcode.dto.TaxCodeResponse;
import com.hisabnikash.erp.masterdata.taxcode.dto.UpdateTaxCodeRequest;
import com.hisabnikash.erp.masterdata.taxcode.infrastructure.TaxCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TaxCodeService {

    private final TaxCodeRepository taxCodeRepository;
    private final MasterDataOwnershipService ownershipService;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_TAX_CODE")
    public TaxCodeResponse create(CreateTaxCodeRequest request) {
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (taxCodeRepository.existsByLegalEntityIdAndCodeIgnoreCase(legalEntityId, request.code())) {
            throw new DuplicateResourceException("Tax code already exists: " + request.code());
        }

        TaxCode taxCode = new TaxCode();
        apply(taxCode, tenantId, legalEntityId, request.code(), request.name(), request.rate(), request.inclusive(), request.active());
        TaxCode saved = taxCodeRepository.save(taxCode);
        TaxCodeResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getTaxCodeCreated(),
                "TaxCodeCreated",
                "TAX_CODE",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public List<TaxCodeResponse> getAll() {
        return ownershipService.filterAccessible(taxCodeRepository.findAll()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaxCode getById(UUID id) {
        TaxCode taxCode = taxCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tax code not found: " + id));
        return ownershipService.requireReadable(taxCode, "Tax code");
    }

    @Auditable(action = "UPDATE_TAX_CODE")
    public TaxCodeResponse update(UUID id, UpdateTaxCodeRequest request) {
        TaxCode taxCode = getById(id);
        String tenantId = ownershipService.requireCurrentTenantId();
        UUID legalEntityId = ownershipService.requireAccessibleLegalEntityId(request.legalEntityId());
        if (taxCodeRepository.existsByLegalEntityIdAndCodeIgnoreCaseAndIdNot(legalEntityId, request.code(), id)) {
            throw new DuplicateResourceException("Tax code already exists: " + request.code());
        }

        apply(taxCode, tenantId, legalEntityId, request.code(), request.name(), request.rate(), request.inclusive(), request.active());
        TaxCode saved = taxCodeRepository.save(taxCode);
        TaxCodeResponse response = toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getTaxCodeUpdated(),
                "TaxCodeUpdated",
                "TAX_CODE",
                saved.getId(),
                response
        );
        return response;
    }

    public TaxCodeResponse toResponse(TaxCode taxCode) {
        return new TaxCodeResponse(
                taxCode.getId(),
                taxCode.getTenantId(),
                taxCode.getLegalEntityId(),
                taxCode.getCode(),
                taxCode.getName(),
                taxCode.getRate(),
                taxCode.isInclusive(),
                taxCode.isActive(),
                taxCode.getCreatedBy(),
                taxCode.getCreatedAt(),
                taxCode.getUpdatedBy(),
                taxCode.getUpdatedAt()
        );
    }

    private void apply(TaxCode taxCode,
                       String tenantId,
                       UUID legalEntityId,
                       String code,
                       String name,
                       BigDecimal rate,
                       boolean inclusive,
                       boolean active) {
        taxCode.setTenantId(tenantId);
        taxCode.setLegalEntityId(legalEntityId);
        taxCode.setCode(code.trim().toUpperCase());
        taxCode.setName(name.trim());
        taxCode.setRate(rate.setScale(4, RoundingMode.HALF_UP));
        taxCode.setInclusive(inclusive);
        taxCode.setActive(active);
    }
}
