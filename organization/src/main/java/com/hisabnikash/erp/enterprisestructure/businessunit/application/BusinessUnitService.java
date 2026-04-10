package com.hisabnikash.erp.enterprisestructure.businessunit.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.businessunit.domain.BusinessUnit;
import com.hisabnikash.erp.enterprisestructure.businessunit.dto.BusinessUnitResponse;
import com.hisabnikash.erp.enterprisestructure.businessunit.dto.CreateBusinessUnitRequest;
import com.hisabnikash.erp.enterprisestructure.businessunit.dto.UpdateBusinessUnitRequest;
import com.hisabnikash.erp.enterprisestructure.businessunit.infrastructure.BusinessUnitRepository;
import com.hisabnikash.erp.enterprisestructure.businessunit.mapper.BusinessUnitMapper;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
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

    private final BusinessUnitRepository repository;
    private final BusinessUnitMapper mapper;
    private final LegalEntityRepository legalEntityRepository;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_BUSINESS_UNIT")
    @CacheEvict(cacheNames = {CacheNames.BUSINESS_UNIT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BusinessUnitResponse create(CreateBusinessUnitRequest request) {
        validateCreate(request);

        BusinessUnit saved = repository.save(mapper.toEntity(request));
        eventPublisher.publish(
                messagingProperties.getTopics().getBusinessUnitCreated(),
                "BusinessUnitCreated",
                "BUSINESS_UNIT",
                saved.getId(),
                mapper.toResponse(saved)
        );
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BusinessUnitResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<BusinessUnit> businessUnits = legalEntityId == null
                ? repository.findAll(pageable)
                : repository.findByLegalEntityId(legalEntityId, pageable);

        return businessUnits.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.BUSINESS_UNIT_BY_ID, key = "#id")
    public BusinessUnitResponse getById(UUID id) {
        return mapper.toResponse(findById(id));
    }

    @Auditable(action = "UPDATE_BUSINESS_UNIT")
    @CacheEvict(cacheNames = {CacheNames.BUSINESS_UNIT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public BusinessUnitResponse update(UUID id, UpdateBusinessUnitRequest request) {
        BusinessUnit businessUnit = findById(id);
        mapper.updateEntity(businessUnit, request);
        BusinessUnit saved = repository.save(businessUnit);
        eventPublisher.publish(
                messagingProperties.getTopics().getBusinessUnitUpdated(),
                "BusinessUnitUpdated",
                "BUSINESS_UNIT",
                saved.getId(),
                mapper.toResponse(saved)
        );
        return mapper.toResponse(saved);
    }

    private void validateCreate(CreateBusinessUnitRequest request) {
        if (!legalEntityRepository.existsById(request.getLegalEntityId())) {
            throw new ResourceNotFoundException("Legal entity not found: " + request.getLegalEntityId());
        }

        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Business unit code already exists: " + request.getCode());
        }
    }

    private BusinessUnit findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business unit not found: " + id));
    }
}
