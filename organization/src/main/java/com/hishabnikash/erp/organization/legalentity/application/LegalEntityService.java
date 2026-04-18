package com.hishabnikash.erp.organization.legalentity.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.common.constants.CacheNames;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.domain.LegalEntity;
import com.hishabnikash.erp.organization.legalentity.dto.ChangeLegalEntityStatusRequest;
import com.hishabnikash.erp.organization.legalentity.dto.CreateLegalEntityRequest;
import com.hishabnikash.erp.organization.legalentity.dto.LegalEntityResponse;
import com.hishabnikash.erp.organization.legalentity.dto.UpdateLegalEntityRequest;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.legalentity.mapper.LegalEntityMapper;
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

    private final LegalEntityRepository legalEntityRepository;
    private final LegalEntityMapper legalEntityMapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;

    @Auditable(action = "CREATE_LEGAL_ENTITY")
    @CacheEvict(cacheNames = {CacheNames.LEGAL_ENTITY_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LegalEntityResponse create(CreateLegalEntityRequest request) {
        validateCreate(request);

        LegalEntity entity = legalEntityMapper.toEntity(request);
        LegalEntity saved = legalEntityRepository.save(entity);
        eventPublisher.publish(
                messagingProperties.getTopics().getLegalEntityCreated(),
                "LegalEntityCreated",
                "LEGAL_ENTITY",
                saved.getId(),
                legalEntityMapper.toResponse(saved)
        );

        return legalEntityMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<LegalEntityResponse> getAll(Pageable pageable) {
        return legalEntityRepository.findAll(pageable)
                .map(legalEntityMapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.LEGAL_ENTITY_BY_ID, key = "#id")
    public LegalEntityResponse getById(UUID id) {
        LegalEntity entity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + id));

        return legalEntityMapper.toResponse(entity);
    }

    @Auditable(action = "UPDATE_LEGAL_ENTITY")
    @CacheEvict(cacheNames = {CacheNames.LEGAL_ENTITY_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LegalEntityResponse update(UUID id, UpdateLegalEntityRequest request) {
        LegalEntity entity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + id));

        validateUpdate(entity.getId(), request);
        legalEntityMapper.updateEntity(entity, request);

        LegalEntity saved = legalEntityRepository.save(entity);
        eventPublisher.publish(
                messagingProperties.getTopics().getLegalEntityUpdated(),
                "LegalEntityUpdated",
                "LEGAL_ENTITY",
                saved.getId(),
                legalEntityMapper.toResponse(saved)
        );

        return legalEntityMapper.toResponse(saved);
    }

    @Auditable(action = "CHANGE_LEGAL_ENTITY_STATUS")
    @CacheEvict(cacheNames = {CacheNames.LEGAL_ENTITY_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public LegalEntityResponse changeStatus(UUID id, ChangeLegalEntityStatusRequest request) {
        LegalEntity entity = legalEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Legal entity not found: " + id));

        legalEntityMapper.updateStatus(entity, request.getStatus());
        LegalEntity saved = legalEntityRepository.save(entity);
        eventPublisher.publish(
                messagingProperties.getTopics().getLegalEntityUpdated(),
                "LegalEntityStatusChanged",
                "LEGAL_ENTITY",
                saved.getId(),
                legalEntityMapper.toResponse(saved)
        );
        return legalEntityMapper.toResponse(saved);
    }

    private void validateCreate(CreateLegalEntityRequest request) {
        if (legalEntityRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Legal entity code already exists: " + request.getCode());
        }

        if (legalEntityRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException(
                    "Registration number already exists: " + request.getRegistrationNumber()
            );
        }
    }

    private void validateUpdate(UUID id, UpdateLegalEntityRequest request) {
        if (legalEntityRepository.existsByRegistrationNumberAndIdNot(request.getRegistrationNumber(), id)) {
            throw new DuplicateResourceException(
                    "Registration number already exists: " + request.getRegistrationNumber()
            );
        }
    }
}
