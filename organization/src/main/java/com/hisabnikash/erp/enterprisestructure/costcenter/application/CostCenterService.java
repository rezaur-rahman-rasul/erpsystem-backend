package com.hisabnikash.erp.enterprisestructure.costcenter.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.costcenter.domain.CostCenter;
import com.hisabnikash.erp.enterprisestructure.costcenter.dto.CostCenterResponse;
import com.hisabnikash.erp.enterprisestructure.costcenter.dto.CreateCostCenterRequest;
import com.hisabnikash.erp.enterprisestructure.costcenter.dto.UpdateCostCenterRequest;
import com.hisabnikash.erp.enterprisestructure.costcenter.infrastructure.CostCenterRepository;
import com.hisabnikash.erp.enterprisestructure.costcenter.mapper.CostCenterMapper;
import com.hisabnikash.erp.enterprisestructure.department.infrastructure.DepartmentRepository;
import com.hisabnikash.erp.enterprisestructure.infrastructure.messaging.EventPublisher;
import com.hisabnikash.erp.enterprisestructure.legalentity.infrastructure.LegalEntityRepository;
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

    private final CostCenterRepository repository;
    private final CostCenterMapper mapper;
    private final EventPublisher eventPublisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;
    private final DepartmentRepository departmentRepository;

    @Auditable(action = "CREATE_COST_CENTER")
    public CostCenterResponse create(CreateCostCenterRequest request) {
        if (repository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Cost center code already exists: " + request.getCode());
        }

        validateReferences(request.getLegalEntityId(), request.getDepartmentId());

        CostCenter saved = repository.save(mapper.toEntity(request));
        CostCenterResponse response = mapper.toResponse(saved);
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
                ? repository.findAll(pageable)
                : repository.findByLegalEntityId(legalEntityId, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CostCenterResponse getById(UUID id) {
        CostCenter costCenter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cost center not found: " + id));
        return mapper.toResponse(costCenter);
    }

    @Auditable(action = "UPDATE_COST_CENTER")
    public CostCenterResponse update(UUID id, UpdateCostCenterRequest request) {
        CostCenter costCenter = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cost center not found: " + id));

        validateReferences(costCenter.getLegalEntityId(), request.getDepartmentId());
        mapper.updateEntity(costCenter, request);

        CostCenter saved = repository.save(costCenter);
        CostCenterResponse response = mapper.toResponse(saved);
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
