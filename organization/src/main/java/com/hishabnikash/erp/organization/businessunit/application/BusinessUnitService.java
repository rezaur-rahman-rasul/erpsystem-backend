package com.hishabnikash.erp.organization.businessunit.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.common.cache.OrganizationLookupCache;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.businessunit.domain.BusinessUnit;
import com.hishabnikash.erp.organization.businessunit.dto.BusinessUnitResponse;
import com.hishabnikash.erp.organization.businessunit.dto.CreateBusinessUnitRequest;
import com.hishabnikash.erp.organization.businessunit.dto.UpdateBusinessUnitRequest;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.businessunit.mapper.BusinessUnitMapper;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessUnitService {

    private final BusinessUnitRepository businessUnitRepository;
    private final BusinessUnitMapper businessUnitMapper;
    private final LegalEntityRepository legalEntityRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final OrganizationLookupCache organizationLookupCache;

    @Auditable(action = "CREATE_BUSINESS_UNIT")
    @CacheEvict(cacheNames = {CacheNames.BUSINESS_UNIT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BusinessUnitResponse create(CreateBusinessUnitRequest request) {
        validateCreate(request);

        BusinessUnit saved = businessUnitRepository.save(businessUnitMapper.toEntity(request));
        eventPublisher.publish(
                messagingProperties.getTopics().getBusinessUnitCreated(),
                "BusinessUnitCreated",
                "BUSINESS_UNIT",
                saved.getId(),
                businessUnitMapper.toResponse(saved)
        );
        return businessUnitMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BusinessUnitResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<BusinessUnit> businessUnits = legalEntityId == null
                ? businessUnitRepository.findAll(pageable)
                : businessUnitRepository.findByLegalEntityId(legalEntityId, pageable);

        return businessUnits.map(businessUnitMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public BusinessUnitResponse getById(UUID id) {
        return organizationLookupCache.findBusinessUnitResponseById(id)
                .getOrThrow(() -> new ResourceNotFoundException("Business unit not found: " + id));
    }

    @Auditable(action = "UPDATE_BUSINESS_UNIT")
    @CacheEvict(cacheNames = {CacheNames.BUSINESS_UNIT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BusinessUnitResponse update(UUID id, UpdateBusinessUnitRequest request) {
        BusinessUnit businessUnit = findById(id);
        businessUnitMapper.updateEntity(businessUnit, request);
        BusinessUnit saved = businessUnitRepository.save(businessUnit);
        eventPublisher.publish(
                messagingProperties.getTopics().getBusinessUnitUpdated(),
                "BusinessUnitUpdated",
                "BUSINESS_UNIT",
                saved.getId(),
                businessUnitMapper.toResponse(saved)
        );
        return businessUnitMapper.toResponse(saved);
    }

    private void validateCreate(CreateBusinessUnitRequest request) {
        if (!legalEntityRepository.existsById(request.getLegalEntityId())) {
            throw new ResourceNotFoundException("Legal entity not found: " + request.getLegalEntityId());
        }

        if (businessUnitRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Business unit code already exists: " + request.getCode());
        }
    }

    private BusinessUnit findById(UUID id) {
        return businessUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business unit not found: " + id));
    }
}
