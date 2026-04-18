package com.hishabnikash.erp.organization.subsidiary.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.subsidiary.domain.Subsidiary;
import com.hishabnikash.erp.organization.subsidiary.dto.CreateSubsidiaryRequest;
import com.hishabnikash.erp.organization.subsidiary.dto.SubsidiaryResponse;
import com.hishabnikash.erp.organization.subsidiary.dto.UpdateSubsidiaryRequest;
import com.hishabnikash.erp.organization.subsidiary.infrastructure.SubsidiaryRepository;
import com.hishabnikash.erp.organization.subsidiary.mapper.SubsidiaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubsidiaryService {

    private final SubsidiaryRepository subsidiaryRepository;
    private final SubsidiaryMapper subsidiaryMapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;

    @Auditable(action = "CREATE_SUBSIDIARY")
    public SubsidiaryResponse create(CreateSubsidiaryRequest request) {
        if (subsidiaryRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Subsidiary code already exists: " + request.getCode());
        }

        validateReferences(request.getParentLegalEntityId(), request.getLegalEntityId());
        Subsidiary saved = subsidiaryRepository.save(subsidiaryMapper.toEntity(request));
        SubsidiaryResponse response = subsidiaryMapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getSubsidiaryCreated(),
                "SubsidiaryCreated",
                "SUBSIDIARY",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public Page<SubsidiaryResponse> getAll(UUID parentLegalEntityId, Pageable pageable) {
        Page<Subsidiary> page = parentLegalEntityId == null
                ? subsidiaryRepository.findAll(pageable)
                : subsidiaryRepository.findByParentLegalEntityId(parentLegalEntityId, pageable);
        return page.map(subsidiaryMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SubsidiaryResponse getById(UUID id) {
        Subsidiary subsidiary = subsidiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subsidiary not found: " + id));
        return subsidiaryMapper.toResponse(subsidiary);
    }

    @Auditable(action = "UPDATE_SUBSIDIARY")
    public SubsidiaryResponse update(UUID id, UpdateSubsidiaryRequest request) {
        Subsidiary subsidiary = subsidiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subsidiary not found: " + id));

        validateReferences(request.getParentLegalEntityId(), subsidiary.getLegalEntityId());
        subsidiaryMapper.updateEntity(subsidiary, request);
        Subsidiary saved = subsidiaryRepository.save(subsidiary);
        SubsidiaryResponse response = subsidiaryMapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getSubsidiaryUpdated(),
                "SubsidiaryUpdated",
                "SUBSIDIARY",
                saved.getId(),
                response
        );
        return response;
    }

    private void validateReferences(UUID parentLegalEntityId, UUID legalEntityId) {
        if (parentLegalEntityId.equals(legalEntityId)) {
            throw new DuplicateResourceException("Subsidiary parent and legal entity cannot be the same");
        }
        if (!legalEntityRepository.existsById(parentLegalEntityId)) {
            throw new ResourceNotFoundException("Parent legal entity not found: " + parentLegalEntityId);
        }
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }
    }
}
