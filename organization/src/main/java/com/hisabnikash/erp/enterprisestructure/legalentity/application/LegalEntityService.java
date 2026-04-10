package com.hisabnikash.erp.enterprisestructure.legalentity.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.domain.LegalEntity;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.ChangeLegalEntityStatusRequest;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.CreateLegalEntityRequest;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.LegalEntityResponse;
import com.hisabnikash.erp.enterprisestructure.legalentity.dto.UpdateLegalEntityRequest;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.legalentity.mapper.LegalEntityMapper;
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
public class LegalEntityService {

    private final LegalEntityRepository repository;
    private final LegalEntityMapper mapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_LEGAL_ENTITY")
    @CacheEvict(cacheNames = {CacheNames.LEGAL_ENTITY_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LegalEntityResponse create(CreateLegalEntityRequest request) {
        validateCreate(request);

        LegalEntity entity = mapper.toEntity(request);
        LegalEntity saved = repository.save(entity);
        eventPublisher.publish(
                messagingProperties.getTopics().getLegalEntityCreated(),
                "LegalEntityCreated",
                "LEGAL_ENTITY",
                saved.getId(),
                mapper.toResponse(saved)
        );

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LegalEntityResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.LEGAL_ENTITY_BY_ID, key = "#id")
    public LegalEntityResponse getById(UUID id) {
        LegalEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + id));

        return mapper.toResponse(entity);
    }

    @Auditable(action = "UPDATE_LEGAL_ENTITY")
    @CacheEvict(cacheNames = {CacheNames.LEGAL_ENTITY_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LegalEntityResponse update(UUID id, UpdateLegalEntityRequest request) {
        LegalEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + id));

        validateUpdate(entity.getId(), request);
        mapper.updateEntity(entity, request);

        LegalEntity saved = repository.save(entity);
        eventPublisher.publish(
                messagingProperties.getTopics().getLegalEntityUpdated(),
                "LegalEntityUpdated",
                "LEGAL_ENTITY",
                saved.getId(),
                mapper.toResponse(saved)
        );

        return mapper.toResponse(saved);
    }

    @Auditable(action = "CHANGE_LEGAL_ENTITY_STATUS")
    @CacheEvict(cacheNames = {CacheNames.LEGAL_ENTITY_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LegalEntityResponse changeStatus(UUID id, ChangeLegalEntityStatusRequest request) {
        LegalEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + id));

        mapper.updateStatus(entity, request.getStatus());
        LegalEntity saved = repository.save(entity);
        eventPublisher.publish(
                messagingProperties.getTopics().getLegalEntityUpdated(),
                "LegalEntityStatusChanged",
                "LEGAL_ENTITY",
                saved.getId(),
                mapper.toResponse(saved)
        );
        return mapper.toResponse(saved);
    }

    private void validateCreate(CreateLegalEntityRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Legal entity code already exists: " + request.getCode());
        }

        if (repository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException(
                    "Registration number already exists: " + request.getRegistrationNumber()
            );
        }
    }

    private void validateUpdate(UUID id, UpdateLegalEntityRequest request) {
        if (repository.existsByRegistrationNumberAndIdNot(request.getRegistrationNumber(), id)) {
            throw new DuplicateResourceException(
                    "Registration number already exists: " + request.getRegistrationNumber()
            );
        }
    }
}
