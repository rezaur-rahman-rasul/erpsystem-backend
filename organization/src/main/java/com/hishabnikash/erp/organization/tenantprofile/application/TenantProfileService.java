package com.hishabnikash.erp.organization.tenantprofile.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.tenantprofile.domain.TenantProfile;
import com.hishabnikash.erp.organization.tenantprofile.dto.CreateTenantProfileRequest;
import com.hishabnikash.erp.organization.tenantprofile.dto.TenantProfileResponse;
import com.hishabnikash.erp.organization.tenantprofile.dto.UpdateTenantProfileRequest;
import com.hishabnikash.erp.organization.tenantprofile.infrastructure.TenantProfileRepository;
import com.hishabnikash.erp.organization.tenantprofile.mapper.TenantProfileMapper;
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

    private final TenantProfileRepository tenantProfileRepository;
    private final TenantProfileMapper tenantProfileMapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;

    @Auditable(action = "CREATE_TENANT_PROFILE")
    public TenantProfileResponse create(CreateTenantProfileRequest request) {
        if (tenantProfileRepository.existsByTenantCodeIgnoreCase(request.getTenantCode())) {
            throw new DuplicateResourceException("Tenant profile code already exists: " + request.getTenantCode());
        }

        validateLegalEntity(request.getLegalEntityId());
        TenantProfile saved = tenantProfileRepository.save(tenantProfileMapper.toEntity(request));
        TenantProfileResponse response = tenantProfileMapper.toResponse(saved);
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
                ? tenantProfileRepository.findAll(pageable)
                : tenantProfileRepository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(tenantProfileMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public TenantProfileResponse getById(UUID id) {
        TenantProfile tenantProfile = tenantProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found: " + id));
        return tenantProfileMapper.toResponse(tenantProfile);
    }

    @Auditable(action = "UPDATE_TENANT_PROFILE")
    public TenantProfileResponse update(UUID id, UpdateTenantProfileRequest request) {
        TenantProfile tenantProfile = tenantProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant profile not found: " + id));

        tenantProfileMapper.updateEntity(tenantProfile, request);
        TenantProfile saved = tenantProfileRepository.save(tenantProfile);
        TenantProfileResponse response = tenantProfileMapper.toResponse(saved);
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
