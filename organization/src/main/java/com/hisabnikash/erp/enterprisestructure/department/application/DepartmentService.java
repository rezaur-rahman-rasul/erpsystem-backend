package com.hisabnikash.erp.enterprisestructure.department.application;

import com.hisabnikash.erp.enterprisestructure.audit.aop.Auditable;
import com.hisabnikash.erp.enterprisestructure.branch.infrastructure.BranchRepository;
import com.hisabnikash.erp.enterprisestructure.common.constants.CacheNames;
import com.hisabnikash.erp.enterprisestructure.common.exception.DuplicateResourceException;
import com.hisabnikash.erp.enterprisestructure.common.exception.ResourceNotFoundException;
import com.hisabnikash.erp.enterprisestructure.config.properties.MessagingProperties;
import com.hisabnikash.erp.enterprisestructure.department.domain.Department;
import com.hisabnikash.erp.enterprisestructure.department.dto.CreateDepartmentRequest;
import com.hisabnikash.erp.enterprisestructure.department.dto.DepartmentResponse;
import com.hisabnikash.erp.enterprisestructure.department.dto.UpdateDepartmentRequest;
import com.hisabnikash.erp.enterprisestructure.department.infrastructure.DepartmentRepository;
import com.hisabnikash.erp.enterprisestructure.department.mapper.DepartmentMapper;
import com.hisabnikash.erp.enterprisestructure.department.validation.DepartmentHierarchyValidator;
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
public class DepartmentService {

    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;
    private final DepartmentHierarchyValidator validator;
    private final EventPublisher publisher;
    private final MessagingProperties messagingProperties;
    private final LegalEntityRepository legalEntityRepository;
    private final BranchRepository branchRepository;

    @Auditable(action = "CREATE_DEPARTMENT")
    @CacheEvict(cacheNames = {CacheNames.DEPARTMENT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public DepartmentResponse create(CreateDepartmentRequest req) {

        if (repository.existsByCode(req.getCode())) {
            throw new DuplicateResourceException("Department code already exists: " + req.getCode());
        }

        validateReferences(req.getLegalEntityId(), req.getBranchId(), req.getParentDepartmentId());
        validator.validateNoCycle(null, req.getParentDepartmentId());

        Department saved = repository.save(mapper.toEntity(req));

        publisher.publish(
                messagingProperties.getTopics().getDepartmentCreated(),
                "DepartmentCreated",
                "DEPARTMENT",
                saved.getId(),
                mapper.toResponse(saved)
        );

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAll(UUID branchId, Pageable pageable) {
        Page<Department> departments = branchId == null
                ? repository.findAll(pageable)
                : repository.findByBranchId(branchId, pageable);
        return departments.map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.DEPARTMENT_BY_ID, key = "#id")
    public DepartmentResponse getById(UUID id) {
        return mapper.toResponse(findById(id));
    }

    @Auditable(action = "UPDATE_DEPARTMENT")
    @CacheEvict(cacheNames = {CacheNames.DEPARTMENT_BY_ID, CacheNames.ORGANIZATION_TREE}, allEntries = true)
    public DepartmentResponse update(UUID id, UpdateDepartmentRequest req) {
        Department d = findById(id);

        validateReferences(d.getLegalEntityId(), req.getBranchId(), req.getParentDepartmentId());
        validator.validateNoCycle(id, req.getParentDepartmentId());
        mapper.updateEntity(d, req);
        Department saved = repository.save(d);
        publisher.publish(
                messagingProperties.getTopics().getDepartmentUpdated(),
                "DepartmentUpdated",
                "DEPARTMENT",
                saved.getId(),
                mapper.toResponse(saved)
        );
        return mapper.toResponse(saved);
    }

    private void validateReferences(UUID legalEntityId, UUID branchId, UUID parentDepartmentId) {
        if (!legalEntityRepository.existsById(legalEntityId)) {
            throw new ResourceNotFoundException("Legal entity not found: " + legalEntityId);
        }

        if (branchId != null && !branchRepository.existsById(branchId)) {
            throw new ResourceNotFoundException("Branch not found: " + branchId);
        }

        if (parentDepartmentId != null && !repository.existsById(parentDepartmentId)) {
            throw new ResourceNotFoundException("Parent department not found: " + parentDepartmentId);
        }
    }

    private Department findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }
}
