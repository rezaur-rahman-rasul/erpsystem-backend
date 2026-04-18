package com.hishabnikash.erp.organization.profitcenter.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.businessunit.infrastructure.BusinessUnitRepository;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import com.hishabnikash.erp.organization.profitcenter.domain.ProfitCenter;
import com.hishabnikash.erp.organization.profitcenter.dto.CreateProfitCenterRequest;
import com.hishabnikash.erp.organization.profitcenter.dto.ProfitCenterResponse;
import com.hishabnikash.erp.organization.profitcenter.dto.UpdateProfitCenterRequest;
import com.hishabnikash.erp.organization.profitcenter.infrastructure.ProfitCenterRepository;
import com.hishabnikash.erp.organization.profitcenter.mapper.ProfitCenterMapper;
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

    private final ProfitCenterRepository profitCenterRepository;
    private final ProfitCenterMapper profitCenterMapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;
    private final BusinessUnitRepository businessUnitRepository;

    @Auditable(action = "CREATE_PROFIT_CENTER")
    public ProfitCenterResponse create(CreateProfitCenterRequest request) {
        if (profitCenterRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Profit center code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getBusinessUnitId());

        ProfitCenter saved = profitCenterRepository.save(profitCenterMapper.toEntity(request));
        ProfitCenterResponse response = profitCenterMapper.toResponse(saved);
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
                ? profitCenterRepository.findAll(pageable)
                : profitCenterRepository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(profitCenterMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProfitCenterResponse getById(UUID id) {
        ProfitCenter profitCenter = profitCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit center not found: " + id));
        return profitCenterMapper.toResponse(profitCenter);
    }

    @Auditable(action = "UPDATE_PROFIT_CENTER")
    public ProfitCenterResponse update(UUID id, UpdateProfitCenterRequest request) {
        ProfitCenter profitCenter = profitCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profit center not found: " + id));

        validateReferences(profitCenter.getLegalEntityId(), request.getBusinessUnitId());
        profitCenterMapper.updateEntity(profitCenter, request);

        ProfitCenter saved = profitCenterRepository.save(profitCenter);
        ProfitCenterResponse response = profitCenterMapper.toResponse(saved);
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
