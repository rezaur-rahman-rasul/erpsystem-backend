package com.hisabnikash.erp.enterprisestructure.profitcenter.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.businessunit.infrastructure.BusinessUnitRepository;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
import com.hisabnikash.erp.enterprisestructure.profitcenter.domain.ProfitCenter;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.CreateProfitCenterRequest;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.ProfitCenterResponse;
import com.hisabnikash.erp.enterprisestructure.profitcenter.dto.UpdateProfitCenterRequest;
import com.hisabnikash.erp.enterprisestructure.profitcenter.infrastructure.ProfitCenterRepository;
import com.hisabnikash.erp.enterprisestructure.profitcenter.mapper.ProfitCenterMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfitCenterService {

    private final ProfitCenterRepository repository;
    private final ProfitCenterMapper mapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;
    private final BusinessUnitRepository businessUnitRepository;

    @Auditable(action = "CREATE_PROFIT_CENTER")
    public ProfitCenterResponse create(CreateProfitCenterRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Profit center code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getBusinessUnitId());

        ProfitCenter saved = repository.save(mapper.toEntity(request));
        ProfitCenterResponse response = mapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getProfitCenterCreated(),
                "ProfitCenterCreated",
                "PROFIT_CENTER",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public Page<ProfitCenterResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<ProfitCenter> page = legalEntityId == null
                ? repository.findAll(pageable)
                : repository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProfitCenterResponse getById(UUID id) {
        ProfitCenter profitCenter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit center not found: " + id));
        return mapper.toResponse(profitCenter);
    }

    @Auditable(action = "UPDATE_PROFIT_CENTER")
    public ProfitCenterResponse update(UUID id, UpdateProfitCenterRequest request) {
        ProfitCenter profitCenter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit center not found: " + id));

        validateReferences(profitCenter.getLegalEntityId(), request.getBusinessUnitId());
        mapper.updateEntity(profitCenter, request);

        ProfitCenter saved = repository.save(profitCenter);
        ProfitCenterResponse response = mapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getProfitCenterUpdated(),
                "ProfitCenterUpdated",
                "PROFIT_CENTER",
                saved.getId(),
                response
        );
        return response;
    }

    private void validateReferences(UUID legalEntityId, UUID businessUnitId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }

        if (businessUnitId != null && !businessUnitRepository.existsById(businessUnitId)) {
            throw new ResourceNotFoundException("Business unit not found: " + businessUnitId);
        }
    }
}
