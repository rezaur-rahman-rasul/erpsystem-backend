package com.hisabnikash.erp.enterprisestructure.subsidiary.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.subsidiary.domain.Subsidiary;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.CreateSubsidiaryRequest;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.SubsidiaryResponse;
import com.hisabnikash.erp.enterprisestructure.subsidiary.dto.UpdateSubsidiaryRequest;
import com.hisabnikash.erp.enterprisestructure.subsidiary.infrastructure.SubsidiaryRepository;
import com.hisabnikash.erp.enterprisestructure.subsidiary.mapper.SubsidiaryMapper;
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

    private final SubsidiaryRepository repository;
    private final SubsidiaryMapper mapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;

    @Auditable(action = "CREATE_SUBSIDIARY")
    public SubsidiaryResponse create(CreateSubsidiaryRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Subsidiary code already exists: " + request.getCode());
        }

        validateReferences(request.getParentLegalEntityId(), request.getLegalEntityId());
        Subsidiary saved = repository.save(mapper.toEntity(request));
        SubsidiaryResponse response = mapper.toResponse(saved);
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
                ? repository.findAll(pageable)
                : repository.findByParentLegalEntityId(parentLegalEntityId, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SubsidiaryResponse getById(UUID id) {
        Subsidiary subsidiary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subsidiary not found: " + id));
        return mapper.toResponse(subsidiary);
    }

    @Auditable(action = "UPDATE_SUBSIDIARY")
    public SubsidiaryResponse update(UUID id, UpdateSubsidiaryRequest request) {
        Subsidiary subsidiary = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subsidiary not found: " + id));

        validateReferences(request.getParentLegalEntityId(), subsidiary.getLegalEntityId());
        mapper.updateEntity(subsidiary, request);
        Subsidiary saved = repository.save(subsidiary);
        SubsidiaryResponse response = mapper.toResponse(saved);
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
