package com.hishabnikash.erp.organization.costcenter.application;

import com.hishabnikash.erp.organization.audit.aop.Auditable;
import com.hishabnikash.erp.organization.common.exception.DuplicateResourceException;
import com.hishabnikash.erp.organization.common.exception.ResourceNotFoundException;
import com.hishabnikash.erp.organization.config.properties.MessagingProperties;
import com.hishabnikash.erp.organization.costcenter.domain.CostCenter;
import com.hishabnikash.erp.organization.costcenter.dto.CostCenterResponse;
import com.hishabnikash.erp.organization.costcenter.dto.CreateCostCenterRequest;
import com.hishabnikash.erp.organization.costcenter.dto.UpdateCostCenterRequest;
import com.hishabnikash.erp.organization.costcenter.infrastructure.CostCenterRepository;
import com.hishabnikash.erp.organization.costcenter.mapper.CostCenterMapper;
import com.hishabnikash.erp.organization.department.infrastructure.DepartmentRepository;
import com.hishabnikash.erp.organization.infrastructure.messaging.EventPublisher;
import com.hishabnikash.erp.organization.legalentity.infrastructure.LegalEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CostCenterService {

    private final CostCenterRepository costCenterRepository;
    private final CostCenterMapper costCenterMapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;
    private final DepartmentRepository departmentRepository;

    @Auditable(action = "CREATE_COST_CENTER")
    public CostCenterResponse create(CreateCostCenterRequest request) {
        if (costCenterRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Cost center code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getDepartmentId());

        CostCenter saved = costCenterRepository.save(costCenterMapper.toEntity(request));
        CostCenterResponse response = costCenterMapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getCostCenterCreated(),
                "CostCenterCreated",
                "COST_CENTER",
                saved.getId(),
                response
        );
        return response;
    }

    @Transactional(readOnly = true)
    public Page<CostCenterResponse> getAll(UUID legalEntityId, Pageable pageable) {
        Page<CostCenter> page = legalEntityId == null
                ? costCenterRepository.findAll(pageable)
                : costCenterRepository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(costCenterMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CostCenterResponse getById(UUID id) {
        CostCenter costCenter = costCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cost center not found: " + id));
        return costCenterMapper.toResponse(costCenter);
    }

    @Auditable(action = "UPDATE_COST_CENTER")
    public CostCenterResponse update(UUID id, UpdateCostCenterRequest request) {
        CostCenter costCenter = costCenterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cost center not found: " + id));

        validateReferences(costCenter.getLegalEntityId(), request.getDepartmentId());
        costCenterMapper.updateEntity(costCenter, request);

        CostCenter saved = costCenterRepository.save(costCenter);
        CostCenterResponse response = costCenterMapper.toResponse(saved);
        eventPublisher.publish(
                messagingProperties.getTopics().getCostCenterUpdated(),
                "CostCenterUpdated",
                "COST_CENTER",
                saved.getId(),
                response
        );
        return response;
    }

    private void validateReferences(UUID legalEntityId, UUID departmentId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }

        if (departmentId != null && !departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department not found: " + departmentId);
        }
    }
}
