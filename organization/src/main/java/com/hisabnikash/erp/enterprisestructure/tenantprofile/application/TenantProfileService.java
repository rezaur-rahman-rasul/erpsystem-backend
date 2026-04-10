package com.hisabnikash.erp.enterprisestructure.tenantprofile.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.domain.TenantProfile;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.dto.CreateTenantProfileRequest;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.dto.TenantProfileResponse;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.dto.UpdateTenantProfileRequest;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.infrastructure.TenantProfileRepository;
import com.hisabnikash.erp.enterprisestructure.tenantprofile.mapper.TenantProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TenantProfileService {

    private final TenantProfileRepository repository;
    private final TenantProfileMapper mapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;

    @Auditable(action = "CREATE_TENANT_PROFILE")
    public TenantProfileResponse create(CreateTenantProfileRequest request) {
        if (repository.existsByTenantCodeIgnoreCase(request.getTenantCode())) {
            throw new DuplicateResourceException("Tenant profile code already exists: " + request.getTenantCode());
        }

        validateLegalEntity(request.getLegalEntityId());
        TenantProfile saved = repository.save(mapper.toEntity(request));
        TenantProfileResponse response = mapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getTenantProfileCreated(),
                "TenantProfileCreated",
                "TENANT_PROFILE",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public Page<TenantProfileResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<TenantProfile> page = legalEntityId == null
                ? repository.findAll(pageable)
                : repository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TenantProfileResponse getById(UUID id) {
        TenantProfile tenantProfile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found: " + id));
        return mapper.toResponse(tenantProfile);
    }

    @Auditable(action = "UPDATE_TENANT_PROFILE")
    public TenantProfileResponse update(UUID id, UpdateTenantProfileRequest request) {
        TenantProfile tenantProfile = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found: " + id));

        mapper.updateEntity(tenantProfile, request);
        TenantProfile saved = repository.save(tenantProfile);
        TenantProfileResponse response = mapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getTenantProfileUpdated(),
                "TenantProfileUpdated",
                "TENANT_PROFILE",
                saved.getId(),
                response
        );
        return response;
    }

    private void validateLegalEntity(UUID legalEntityId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }
    }
}
